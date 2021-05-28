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
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  Renderer2,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { NgGridConfig, NgGridItemConfig } from 'angular2-grid';
import * as Stomp from '@stomp/stompjs';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { ProjectWidgetPositionRequest } from '../../../shared/models/backend/project-widget/project-widget-position-request';
import { GridItemUtils } from '../../../shared/utils/grid-item.utils';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { LibraryService } from '../../services/library/library.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import Swipe from 'swipejs';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'suricate-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  styleUrls: ['./dashboard-screen.component.scss']
})
export class DashboardScreenComponent implements AfterViewInit, OnChanges, OnDestroy {
  /**
   * Reference on the span containing all the required JS libraries
   */
  @ViewChild('externalJsLibraries')
  public externalJsLibrariesSpan: ElementRef<HTMLSpanElement>;

  /**
   * The project to display
   */
  @Input()
  public project: Project;

  /**
   * The project widget list
   */
  @Input()
  public projectWidgets: ProjectWidget[];

  /**
   * Tell if the dashboard should be on readOnly or not
   */
  @Input()
  public readOnly = true;

  /**
   * The screen code
   */
  @Input()
  public screenCode: number;

  /**
   * Event for handling the disconnection
   */
  @Output()
  public disconnectEvent = new EventEmitter<void>();

  /**
   * Use to tell to the parent component that he should refresh the project widgets
   */
  @Output()
  public refreshProjectWidget = new EventEmitter<void>();

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * The options for the plugin angular2-grid
   */
  public gridOptions: NgGridConfig = {};

  /**
   * Grid state when widgets were first loaded
   */
  protected startGridStackItems: NgGridItemConfig[] = [];

  /**
   * The grid items description
   */
  public gridStackItems: NgGridItemConfig[] = [];

  /**
   * Tell if we should display the screen code
   */
  public shouldDisplayScreenCode = false;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  public numberOfGrids = Array.from(Array(3).keys());

  /**
   * The constructor
   *
   * @param renderer The renderer Angular entity
   * @param httpProjectService Back-End service used to manage the project
   * @param dashboardService Front-End service used to manage the dashboard
   * @param websocketService Front-End service used to manage the web sockets
   * @param libraryService Front-End service used to manage the libraries
   */
  constructor(
    private renderer: Renderer2,
    private readonly httpProjectService: HttpProjectService,
    private readonly dashboardService: DashboardService,
    private readonly websocketService: WebsocketService,
    private readonly libraryService: LibraryService
  ) {}

  /**
   * Each time a value change, this function will be called
   */
  public ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      if (!changes.project.previousValue) {
        // Inject this variable in the window scope because some widgets use it to init the js
        (window as any).page_loaded = true;
      }

      this.project = changes.project.currentValue;
      this.initGridStackOptions();

      if (changes.project.previousValue) {
        if (changes.project.previousValue.token !== changes.project.currentValue.token) {
          this.resetWebsocketSubscriptions();
        }
      } else {
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
   * After view init method
   */
  public ngAfterViewInit(): void {
    this.addExternalJSLibrariesToTheDOM();

    const element = document.getElementById('slider');
    (window as any).mySwipe = new Swipe(element, {
      startSlide: 0,
      auto: 3000,
      draggable: false,
      autoRestart: false,
      continuous: true,
      disableScroll: true,
      stopPropagation: true,
      callback: function(index, element) {},
      transitionEnd: function(index, element) {}
    });
  }

  /**
   * When the component is destroyed (new page)
   */
  public ngOnDestroy(): void {
    this.disconnectFromWebsocket();
  }

  /**
   * For each JS libraries linked with the project, create a script element with the URL of the library
   * and a callback which notify subscribers when the library is loaded.
   */
  public addExternalJSLibrariesToTheDOM() {
    this.libraryService.numberOfExternalLibrariesToLoad = this.project.librariesToken.length;

    if (this.project.librariesToken.length > 0) {
      this.project.librariesToken.forEach(token => {
        const script: HTMLScriptElement = document.createElement('script');
        script.type = 'text/javascript';
        script.src = HttpAssetService.getContentUrl(token);
        script.onload = () => this.libraryService.markScriptAsLoaded(token);
        script.async = false;

        this.renderer.appendChild(this.externalJsLibrariesSpan.nativeElement, script);
      });
    }

    // No library to load
    else {
      this.libraryService.emitAreJSScriptsLoaded(true);
    }
  }

  /**
   * Display the screen code
   */
  private displayScreenCode(): void {
    this.shouldDisplayScreenCode = true;
    setTimeout(() => (this.shouldDisplayScreenCode = false), 10000);
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
        col: projectWidget.widgetPosition.gridColumn,
        row: projectWidget.widgetPosition.gridRow,
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
   * Init the websocket subscriptions
   */
  private initWebsocketSubscriptions(): void {
    this.websocketProjectEventSubscription();
    this.websocketScreenEventSubscription();
  }

  /**
   * Reset the websocket subscription
   */
  private resetWebsocketSubscriptions(): void {
    this.unsubscribeToWebsocket();
    this.initWebsocketSubscriptions();
  }

  /**
   * Disconnect from web sockets
   */
  private disconnectFromWebsocket(): void {
    this.unsubscribeToWebsocket();
    this.websocketService.disconnect();
  }

  /**
   * Unsubscribe to every current websocket connections
   */
  private unsubscribeToWebsocket(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Create a websocket subscription for the current project
   */
  private websocketProjectEventSubscription(): void {
    const projectSubscriptionUrl = `/user/${this.project.token}/queue/live`;

    this.websocketService
      .watch(projectSubscriptionUrl)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        switch (updateEvent.type) {
          case WebsocketUpdateTypeEnum.RELOAD:
            location.reload();
            break;
          case WebsocketUpdateTypeEnum.DISPLAY_NUMBER:
            this.displayScreenCode();
            break;
          case WebsocketUpdateTypeEnum.POSITION:
            this.refreshProjectWidget.emit();
            break;
          case WebsocketUpdateTypeEnum.DISCONNECT:
            this.disconnectFromWebsocket();
            this.disconnectEvent.emit();
            break;
          default:
            this.refreshProjectWidget.emit();
        }
      });
  }

  /**
   * Create a websocket subscription for the current screen
   */
  private websocketScreenEventSubscription(): void {
    const screenSubscriptionUrl = `/user/${this.project.token}-${this.screenCode}/queue/unique`;

    this.websocketService
      .watch(screenSubscriptionUrl)
      .pipe(takeUntil(this.unsubscribe))
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
          gridColumn: gridStackItem.col,
          gridRow: gridStackItem.row,
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
