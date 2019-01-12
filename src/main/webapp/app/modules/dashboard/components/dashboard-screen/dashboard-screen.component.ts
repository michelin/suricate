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


import {Component, ElementRef, EventEmitter, HostBinding, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges,} from '@angular/core';
import {takeWhile} from 'rxjs/operators';
import * as Stomp from '@stomp/stompjs';

import {Project} from '../../../../shared/model/api/project/Project';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {WebsocketService} from '../../../../shared/services/websocket.service';
import {HttpAssetService} from '../../../../shared/services/api/http-asset.service';
import {WSUpdateEvent} from '../../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../../shared/model/websocket/enums/WSUpdateType';
import {DashboardService} from '../../dashboard.service';
import {NgGridConfig, NgGridItemConfig} from 'angular2-grid';
import {ProjectWidgetPositionRequest} from '../../../../shared/model/api/ProjectWidget/ProjectWidgetPositionRequest';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {Subscription} from 'rxjs';
import {RunScriptsDirective} from '../../../../shared/directives/run-scripts.directive';
import {RunScriptsService} from '../../../../shared/directives/run-scripts.service';


/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'app-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  styleUrls: ['./dashboard-screen.component.css']
})
export class DashboardScreenComponent implements OnInit, OnChanges, OnDestroy {

  /**
   * Tell to subscriptions if the component is alive
   * When the component is destroyed, the associated subscriptions will be deleted
   */
  private isAlive = true;

  /**
   * The project to display
   * @type {Project}
   */
  @Input() project: Project;
  /**
   * The project widget list
   * @type {ProjectWidget[]}
   */
  @Input() projectWidgets: ProjectWidget[];
  /**
   * Tell if the dashboard should be on readOnly or not
   * @type {boolean}
   */
  @Input() readOnly = true;
  /**
   * The screen code
   * @type {number}
   */
  @Input() screenCode: number;

  /**
   * Event for handling the disconnection
   * @type {EventEmitter<any>}
   */
  @Output() disconnectEvent = new EventEmitter<any>();

  /**
   * Add runScriptsDirective, so we can recall it
   */
  @HostBinding('attr.appRunScripts') appRunScriptDirective = new RunScriptsDirective(this.elementRef, this.runScriptsService);

  /**
   * The stompJS Subscription for project event
   */
  projectEventSubscription: Subscription;

  /**
   * The stompJS Subscription for screen event
   */
  screenEventSubscription: Subscription;

  /**
   * Tell if we should display the screen code
   * @type {boolean}
   */
  shouldDisplayScreenCode = false;

  /**
   * The options for the plugin angular2-grid
   * @type {NgGridConfig}
   */
  gridOptions: NgGridConfig = {};

  /**
   * The grid items description
   * @type {NgGridItemConfig[]}
   */
  gridStackItems: NgGridItemConfig[] = [];

  /**
   * Tell if the global JS scripts has been rendered
   * (Online JS scripts contained inside the widgets are executed when this value is set to true)
   */
  isSrcScriptsRendered = false;

  /**
   * The constructor
   *
   * @param websocketService The websocket service
   * @param httpAssetService The http asset service
   * @param httpProjectService The http project service
   * @param dashboardService The dashboard service
   * @param elementRef The element Ref service
   * @param runScriptsService The service associated to the runScript directive
   */
  constructor(private websocketService: WebsocketService,
              private httpAssetService: HttpAssetService,
              private httpProjectService: HttpProjectService,
              private dashboardService: DashboardService,
              private elementRef: ElementRef,
              private runScriptsService: RunScriptsService) {
  }

  /**********************************************************************************************************/
  /*                      COMPONENT LIFE CYCLE                                                              */

  /**********************************************************************************************************/
  /**
   * Called when the component is init
   */
  ngOnInit(): void {
    this.runScriptsService.scriptRenderedEvent().subscribe(isRendered => {
      this.isSrcScriptsRendered = isRendered;
    });

    // We have to inject this variable in the window scope (because some Widgets use it for init the js)
    window['page_loaded'] = true;
  }

  /**
   * Each time a value change, this function will be called
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      this.runScriptsService.emitScriptRendered(false);
      this.project = changes.project.currentValue;
      this.initGridStackOptions(this.project);
      this.appRunScriptDirective.ngOnInit();

      if (changes.project.previousValue) {
        if (changes.project.previousValue.token !== changes.project.currentValue.token) {
          this.resetWebsocketSubscriptions();
        }
      } else {
        this.startWebsocketConnection();
        this.initWebsocketSubscriptions();
      }
    }

    if (changes.projectWidgets) {
      this.projectWidgets = changes.projectWidgets.currentValue;
      this.initGridStackItems();
    }
  }

  /**
   * When the component is destroyed (new page)
   */
  ngOnDestroy(): void {
    this.disconnectFromWebsocket();
  }

  /**
   * Display the screen code
   */
  displayScreenCode() {
    this.shouldDisplayScreenCode = true;
    setTimeout(() => this.shouldDisplayScreenCode = false, 10000);
  }

  /**********************************************************************************************************/
  /*                      GRID STACK MANAGEMENT                                                             */

  /**********************************************************************************************************/

  /**
   * Init the options for Grid Stack plugin
   *
   * @param {Project} project The project used for the initialization
   */
  initGridStackOptions(project: Project): void {
    this.gridOptions = {
      'max_cols': project.gridProperties.maxColumn,
      'min_cols': 1,
      'row_height': project.gridProperties.widgetHeight,
      'margins': [4],
      'auto_resize': true
    };

    if (this.readOnly) {
      this.gridOptions = {
        ...this.gridOptions,
        'draggable': false,
        'resizable': false,
      };
    }
  }

  /**
   * Create the list of gridStackItems used to display widgets on the grid
   */
  initGridStackItems(): void {
    this.gridStackItems = [];
    this.projectWidgets.forEach((projectWidget: ProjectWidget) => {
      this.gridStackItems.push({
        col: projectWidget.widgetPosition.col,
        row: projectWidget.widgetPosition.row,
        sizey: projectWidget.widgetPosition.height,
        sizex: projectWidget.widgetPosition.width,
        payload: projectWidget
      });
    });
  }

  /**********************************************************************************************************/
  /*                      JS MANAGEMENT                                                                     */

  /**********************************************************************************************************/

  /**
   * Get the JS libraries from project
   *
   * @returns {string} The src script
   */
  getJSLibraries(): string {
    let scriptUrls = '';

    if (this.project.librariesToken) {
      this.project.librariesToken.forEach(libraryToken => {
        scriptUrls = scriptUrls.concat(`<script src="${this.httpAssetService.getContentUrl(libraryToken)}"></script>`);
      });
    }

    return scriptUrls;
  }

  /**********************************************************************************************************/
  /*                      WEBSOCKET MANAGEMENT                                                              */

  /**********************************************************************************************************/

  /**
   * Start the websocket connection using sockJS
   */
  startWebsocketConnection() {
    this.websocketService.startConnection();
  }

  /**
   * Disconnect from websockets
   */
  disconnectFromWebsocket() {
    this.unsubscribeToWebsocket();
    this.websocketService.disconnect();
  }

  /**
   * Init the websocket subscriptions
   */
  initWebsocketSubscriptions() {
    this.isAlive = true;
    this.websocketProjectEventSubscription();
    this.websocketScreenEventSubscription();
  }

  /**
   * Unsubscribe to every current websocket connections
   */
  unsubscribeToWebsocket() {
    this.projectEventSubscription.unsubscribe();
    this.projectEventSubscription = null;
    this.screenEventSubscription.unsubscribe();
    this.screenEventSubscription = null;
    this.isAlive = false;
  }

  /**
   * Reset the websocket subscription
   */
  resetWebsocketSubscriptions() {
    this.unsubscribeToWebsocket();
    this.initWebsocketSubscriptions();
  }

  /**
   * Create a websocket subscription for the current project
   */
  websocketProjectEventSubscription() {
    const projectSubscriptionUrl = `/user/${this.project.token}/queue/live`;

    this.projectEventSubscription = this.websocketService.subscribeToDestination(projectSubscriptionUrl).pipe(
      takeWhile(() => this.isAlive)
    ).subscribe((stompMessage: Stomp.Message) => {
      const updateEvent: WSUpdateEvent = JSON.parse(stompMessage.body);

      if (updateEvent.type === WSUpdateType.RELOAD) {
        location.reload();
      } else if (updateEvent.type === WSUpdateType.DISPLAY_NUMBER) {
        this.displayScreenCode();
      } else {
        this.dashboardService.refreshProject();
      }
    });
  }

  /**
   * Create a websocket subscription for the current screen
   */
  websocketScreenEventSubscription() {
    const screenSubscriptionUrl = `/user/${this.project.token}-${this.screenCode}/queue/unique`;

    this.screenEventSubscription = this.websocketService.subscribeToDestination(screenSubscriptionUrl).pipe(
      takeWhile(() => this.isAlive)
    ).subscribe((stompMessage: Stomp.Message) => {
      const updateEvent: WSUpdateEvent = JSON.parse(stompMessage.body);

      if (updateEvent.type === WSUpdateType.DISCONNECT) {
        this.disconnectFromWebsocket();
        this.disconnectEvent.emit();
      }
    });
  }

  /**
   * Update the project widget position
   */
  updateProjectWidgetsPosition() {
    console.log(`update positions: ${this.gridStackItems}`);

    const projectWidgetPositionRequests: ProjectWidgetPositionRequest[] = [];

    this.gridStackItems.forEach(gridStackItem => {
      projectWidgetPositionRequests.push({
        projectWidgetId: (gridStackItem.payload as ProjectWidget).id,
        col: gridStackItem.col,
        row: gridStackItem.row,
        height: gridStackItem.sizey,
        width: gridStackItem.sizex,
      });
    });

    this.httpProjectService.updateProjectWidgetPositions(this.project.token, projectWidgetPositionRequests).subscribe();
  }
}