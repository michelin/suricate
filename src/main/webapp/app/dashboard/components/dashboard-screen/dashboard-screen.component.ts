/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import { takeWhile } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { NgGridConfig, NgGridItemConfig } from 'angular2-grid';
import * as Stomp from '@stomp/stompjs';

import { Project } from '../../../shared/models/backend/project/project';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { WebsocketService } from '../../../shared/services/frontend/websocket.service';
import { HttpAssetService } from '../../../shared/services/backend/http-asset.service';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { DashboardService } from '../../services/dashboard.service';
import { ProjectWidgetPositionRequest } from '../../../shared/models/backend/project-widget/project-widget-position-request';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { RunScriptsDirective } from '../../../shared/directives/run-scripts.directive';
import { GridItemUtils } from '../../../shared/utils/grid-item.utils';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { LibraryService } from '../../services/library.service';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'suricate-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  styleUrls: ['./dashboard-screen.component.scss']
})
export class DashboardScreenComponent implements OnInit, OnChanges, OnDestroy {
  /**
   * The project to display
   * @type {Project}
   * @public
   */
  @Input()
  public project: Project;

  /**
   * The project widget list
   * @type {ProjectWidget[]}
   * @public
   */
  @Input()
  public projectWidgets: ProjectWidget[];

  /**
   * Tell if the dashboard should be on readOnly or not
   * @type {boolean}
   * @public
   */
  @Input()
  public readOnly = true;

  /**
   * The screen code
   * @type {number}
   * @public
   */
  @Input()
  public screenCode: number;

  /**
   * Event for handling the disconnection
   * @type {EventEmitter<void>}
   * @public
   */
  @Output()
  public disconnectEvent = new EventEmitter<void>();

  /**
   * Use to tell to the parent component that he should refresh the project widgets
   * @type {EventEmitter<void>}
   * @public
   */
  @Output()
  public refreshProjectWidget = new EventEmitter<void>();

  /**
   * Add runScriptsDirective, so we can recall it
   * @type {RunScriptsDirective}
   * @public
   */
  @HostBinding('attr.appRunScripts')
  public appRunScriptDirective = new RunScriptsDirective(this.elementRef, this.libraryService);

  /**
   * Tell to subscriptions if the component is alive
   * When the component is destroyed, the associated subscriptions will be deleted
   */
  private isAlive = true;

  /**
   * The options for the plugin angular2-grid
   * @type {NgGridConfig}
   * @protected
   */
  public gridOptions: NgGridConfig = {};

  /**
   * Grid state when widgets were first loaded
   * @type {NgGridItemConfig[]}
   * @protected
   */
  protected startGridStackItems: NgGridItemConfig[] = [];

  /**
   * The grid items description
   * @type {NgGridItemConfig[]}
   * @protected
   */
  public gridStackItems: NgGridItemConfig[] = [];

  /**
   * The stompJS Subscription for screen event
   * @type {Subscription}
   * @private
   */
  private screenEventSubscription: Subscription;

  /**
   * Tell if we should display the screen code
   * @type {boolean}
   * @protected
   */
  public shouldDisplayScreenCode = false;

  /**
   * The list of icons
   * @type {IconEnum}
   * @protected
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   * @type {MaterialIconRecords}
   * @protected
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * URLs of the JS libraries required by the widgets
   */
  public librariesURLs = '';

  /**
   * The constructor
   *
   * @param elementRef Angular service used to inject a reference on the component
   * @param httpProjectService Suricate service used to manage project
   * @param dashboardService Frontend service used to manage dashboards
   * @param websocketService Frontend service used to manage websocket connections
   * @param libraryService Frontend service used to manage the libraries
   */
  constructor(
    private readonly elementRef: ElementRef,
    private readonly httpProjectService: HttpProjectService,
    private readonly dashboardService: DashboardService,
    private readonly websocketService: WebsocketService,
    private readonly libraryService: LibraryService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.loadJSLibrariesFromTokens();
  }

  /**
   * Each time a value change, this function will be called
   */
  public ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      if (!changes.project.previousValue) {
        // We have to inject this variable in the window scope (because some Widgets use it for init the js)
        window['page_loaded'] = true;
      }

      this.project = changes.project.currentValue;
      this.appRunScriptDirective.ngOnInit();
      setTimeout(() => this.initGridStackOptions());

      if (changes.project.previousValue) {
        if (changes.project.previousValue.token !== changes.project.currentValue.token) {
          this.resetWebsocketSubscriptions();
        }
      } else {
        this.startWebsocketConnection();
        this.initWebsocketSubscriptions();
      }
    }

    if (changes.readOnly) {
      this.readOnly = changes.readOnly.currentValue;
      this.initGridStackOptions();
    }

    if (changes.projectWidgets) {
      this.projectWidgets = changes.projectWidgets.currentValue;
      this.initGridStackItems();
    }
  }

  /**
   * When the component is destroyed (new page)
   */
  public ngOnDestroy(): void {
    this.disconnectFromWebsocket();
  }

  /**
   * Display the screen code
   */
  private displayScreenCode(): void {
    this.shouldDisplayScreenCode = true;
    setTimeout(() => (this.shouldDisplayScreenCode = false), 10000);
  }

  /**
   * Build the list of JS libraries required by the widgets of the project
   */
  public loadJSLibrariesFromTokens() {
    if (this.project.librariesToken) {
      this.project.librariesToken.forEach(libraryToken => {
        this.librariesURLs = this.librariesURLs.concat(`<script src="${HttpAssetService.getContentUrl(libraryToken)}"></script>`);
      });
    }
  }

  /**********************************************************************************************************/
  /*                      GRID STACK MANAGEMENT                                                             */

  /**********************************************************************************************************/

  /**
   * Init the options for Grid Stack plugin
   */
  private initGridStackOptions(): void {
    this.gridOptions = {
      visible_cols: this.project.gridProperties.maxColumn,
      min_cols: 1,
      row_height: this.project.gridProperties.widgetHeight,
      min_rows: 1,
      margins: [4],
      auto_resize: true,
      draggable: false,
      resizable: false
    };

    if (!this.readOnly) {
      this.gridOptions = {
        ...this.gridOptions,
        draggable: true,
        resizable: true
      };
    }
  }

  /**
   * Create the list of gridStackItems used to display widgets on the grid
   */
  private initGridStackItems(): void {
    this.gridStackItems = [];

    if (this.projectWidgets) {
      this.startGridStackItems = this.getGridStackItemsFromProjectWidgets(this.projectWidgets);
      // Make a copy with a new reference
      this.gridStackItems = JSON.parse(JSON.stringify(this.startGridStackItems));
    }
  }

  /**
   * Get the list of GridItemConfigs from project widget
   *
   * @param projectWidgets The project widgets
   */
  private getGridStackItemsFromProjectWidgets(projectWidgets: ProjectWidget[]): NgGridItemConfig[] {
    const gridStackItemsConfig: NgGridItemConfig[] = [];

    this.projectWidgets.forEach((projectWidget: ProjectWidget) => {
      gridStackItemsConfig.push({
        col: projectWidget.widgetPosition.col,
        row: projectWidget.widgetPosition.row,
        sizey: projectWidget.widgetPosition.height,
        sizex: projectWidget.widgetPosition.width,
        payload: projectWidget
      });
    });

    return gridStackItemsConfig;
  }

  /**********************************************************************************************************/
  /*                      WEBSOCKET MANAGEMENT                                                              */

  /**********************************************************************************************************/

  /**
   * Start the websocket connection using sockJS
   */
  private startWebsocketConnection(): void {
    this.websocketService.startConnection();
  }

  /**
   * Disconnect from websockets
   */
  private disconnectFromWebsocket(): void {
    this.unsubscribeToWebsocket();
    this.websocketService.disconnect();
  }

  /**
   * Init the websocket subscriptions
   */
  private initWebsocketSubscriptions(): void {
    this.isAlive = true;
    this.websocketProjectEventSubscription();
    this.websocketScreenEventSubscription();
  }

  /**
   * Unsubscribe to every current websocket connections
   */
  private unsubscribeToWebsocket(): void {
    if (this.screenEventSubscription) {
      this.screenEventSubscription.unsubscribe();
      this.screenEventSubscription = null;
    }

    this.isAlive = false;
  }

  /**
   * Reset the websocket subscription
   */
  private resetWebsocketSubscriptions(): void {
    this.unsubscribeToWebsocket();
    this.initWebsocketSubscriptions();
  }

  /**
   * Create a websocket subscription for the current project
   */
  private websocketProjectEventSubscription(): void {
    const projectSubscriptionUrl = `/user/${this.project.token}/queue/live`;

    this.websocketService
      .subscribeToDestination(projectSubscriptionUrl)
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.RELOAD) {
          location.reload();
        } else if (updateEvent.type === WebsocketUpdateTypeEnum.DISPLAY_NUMBER) {
          this.displayScreenCode();
        } else if (updateEvent.type === WebsocketUpdateTypeEnum.POSITION) {
          this.refreshProjectWidget.emit();
        } else if (updateEvent.type === WebsocketUpdateTypeEnum.DISCONNECT) {
          this.disconnectFromWebsocket();
          this.disconnectEvent.emit();
        } else {
          this.refreshProjectWidget.emit();
        }
      });
  }

  /**
   * Create a websocket subscription for the current screen
   */
  private websocketScreenEventSubscription(): void {
    const screenSubscriptionUrl = `/user/${this.project.token}-${this.screenCode}/queue/unique`;

    this.screenEventSubscription = this.websocketService
      .subscribeToDestination(screenSubscriptionUrl)
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.DISCONNECT) {
          this.disconnectFromWebsocket();
          this.disconnectEvent.emit();
        }
      });
  }

  /**
   * Update the project widget position
   */
  public updateProjectWidgetsPosition(): void {
    if (this.isGridItemsHasMoved()) {
      const projectWidgetPositionRequests: ProjectWidgetPositionRequest[] = [];
      this.gridStackItems.forEach(gridStackItem => {
        projectWidgetPositionRequests.push({
          projectWidgetId: (gridStackItem.payload as ProjectWidget).id,
          col: gridStackItem.col,
          row: gridStackItem.row,
          height: gridStackItem.sizey,
          width: gridStackItem.sizex
        });
      });

      this.httpProjectService.updateProjectWidgetPositions(this.project.token, projectWidgetPositionRequests).subscribe();
    }
  }

  /**
   * Checks if the grid elements have been moved
   */
  private isGridItemsHasMoved(): boolean {
    let itemHaveBeenMoved = false;

    this.startGridStackItems.forEach(startGridItem => {
      const gridItemFound = this.gridStackItems.find(currentGridItem => {
        return (currentGridItem.payload as ProjectWidget).id === (startGridItem.payload as ProjectWidget).id;
      });

      if (gridItemFound && GridItemUtils.isItemHaveBeenMoved(startGridItem, gridItemFound)) {
        itemHaveBeenMoved = true;
      }
    });

    return itemHaveBeenMoved;
  }
}
