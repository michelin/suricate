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

import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DashboardService} from '../dashboard.service';
import {Project} from '../../../shared/model/dto/Project';
import {Widget} from '../../../shared/model/dto/Widget';
import {DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {Observable} from 'rxjs/Observable';
import {takeWhile} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import {AuthenticationService} from '../../authentication/authentication.service';
import {AbstractHttpService} from '../../../shared/services/abstract-http.service';
import {WSConfiguration} from '../../../shared/model/websocket/WSConfiguration';
import {WebsocketService} from '../../../shared/services/websocket.service';
import {Subscription} from 'rxjs/Subscription';
import {NumberUtils} from '../../../shared/utils/NumberUtils';
import {WSUpdateEvent} from '../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../shared/model/websocket/enums/WSUpdateType';
import {ProjectWidget} from '../../../shared/model/dto/ProjectWidget';

/**
 * Component that display a specific dashboard
 */
@Component({
  selector: 'app-dashboard-detail',
  templateUrl: './dashboard-detail.component.html',
  styleUrls: ['./dashboard-detail.component.css']
})
export class DashboardDetailComponent implements OnInit, OnDestroy {

  /**
   * Define the min bound for the screen code random generation
   *
   * @type {number} The min bound
   */
  private readonly MIN_SCREEN_CODE_BOUND = 100000;

  /**
   * Define the max bound for the screen code random generation
   *
   * @type {number} The max bound
   */
  private readonly MAX_SCREEN_CODE_BOUND = 999999;

  /**
   * Used for keep the subscription of subjects/Obsevables open
   *
   * @type {boolean} True if we keep the connection, False if we have to unsubscribe
   */
  isAlive = true;

  /**
   * The project as observable
   */
  project$: Observable<Project>;

  /**
   * The options for the plugin angular2-grid
   */
  gridOptions: {};

  /**
   * Save every web socket subscriptions event
   */
  websocketSubscriptions: Subscription[] = [];

  /**
   * Screen code used for websocket communication as clientId
   */
  screenCode: number;

  /**
   * constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   * @param {DomSanitizer} domSanitizer The domSanitizer service
   * @param {WebsocketService} websocketService The websocket service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private dashboardService: DashboardService,
              private changeDetectorRef: ChangeDetectorRef,
              private domSanitizer: DomSanitizer,
              private websocketService: WebsocketService) { }

  /**
   * Init objects
   */
  ngOnInit() {
    // init current dashboard
    this.subcribeToProjectSubject();

    // Global init from project
    this.activatedRoute.params.subscribe( params => {
      this.dashboardService
          .getOneById(params['id'])
          .subscribe(project => {
            this.initGridStackOptions(project);
            // Unsubcribe every websockets if we have change of dashboard
            this.unsubscribeToWebsockets();
            // Screen code generation
            this.screenCode = NumberUtils.getRandomIntBetween(this.MIN_SCREEN_CODE_BOUND, this.MAX_SCREEN_CODE_BOUND);
            // Subscribe to the new dashboard
            this.createWebsocketConnection(project);
            this.dashboardService.currendDashbordSubject.next(project);
          });
    });
  }

  /**
   * Init the Project subject subscription
   */
  subcribeToProjectSubject() {
    this
        .dashboardService
        .currendDashbordSubject
        .pipe(takeWhile(() => this.isAlive))
        .subscribe(project => this.project$ = of(project) );
  }

  /**
   * Init the options for Grid Stack plugin
   *
   * @param {Project} project The project used for the initialization
   */
  initGridStackOptions(project: Project) {
    this.gridOptions = {
      'max_cols': project.maxColumn,
      'min_cols': 1,
      'row_height': project.widgetHeight / 1.5,
      'margins': [5],
      'auto_resize': true
    };
  }

  /**
   * Create the dashboard websocket connection
   *
   * @param {Project} project The project wanted for the connection
   */
  createWebsocketConnection(project: Project) {
    const websocketConfiguration: WSConfiguration = {
      host: `${AbstractHttpService.BASE_WS_URL}?${AbstractHttpService.SPRING_ACCESS_TOKEN_ENPOINT}=${AuthenticationService.getToken()}`,
      debug: true,
      queue: {'init': false}
    };

    this.websocketService
        .connect(websocketConfiguration)
        .subscribe(() => {
          const uniqueSubscription: Subscription = this.websocketService
                                                       .subscribe(
                                                           `/user/${project.token}-${this.screenCode}/queue/unique`,
                                                           this.handleUniqueScreenEvent.bind(this)
                                                       );

          const globalSubscription: Subscription = this.websocketService
                                                       .subscribe(
                                                           `/user/${project.token}/queue/live`,
                                                           this.handleGlobalScreenEvent.bind(this)
                                                       );

          this.websocketSubscriptions.push(uniqueSubscription);
          this.websocketSubscriptions.push(globalSubscription);
        });
  }

  /**
   * Manage the event sent by the server (destination : A specified screen)
   *
   * @param {WSUpdateEvent} updateEvent The message received
   * @param headers The headers of the websocket event
   */
  handleUniqueScreenEvent(updateEvent: WSUpdateEvent, headers: any) {
    console.log(`uniqueScreenEvent - ${updateEvent}`);
  }

  /**
   * Manage the event sent by the server (destination : Every screen connected to this project)
   *
   * @param {WSUpdateEvent} updateEvent The message received
   * @param headers The headers of the websocket event
   */
  handleGlobalScreenEvent(updateEvent: WSUpdateEvent, headers: any) {
    if (updateEvent.type === WSUpdateType.WIDGET) {
      this.dashboardService.updateWidgetHtmlFromProjetWidgetId(updateEvent.content.id, updateEvent.content.instantiateHtml);
    }
  }

  /**
   * Unsubcribe and disconnect from websockets
   */
  unsubscribeToWebsockets() {
    this.websocketSubscriptions.forEach( (websocketSubscription: Subscription, index: number) => {
      this.websocketService.unsubscribe(websocketSubscription);
      this.websocketSubscriptions.splice(index, 1);
    });

    this.websocketService.disconnect();
  }

  /**
   * Get the CSS for the grid
   *
   * @param {Project} project The project
   * @returns {SafeHtml} The css as safe html
   */
  getGridCSS(css: string): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustHtml(`
      <style>
        .grid {
          ${css}
        }
      </style>
    `);
  }

  /**
   * Get the html/CSS code for the widget
   *
   * @param {ProjectWidget} projectWidget The widget
   * @returns {SafeHtml} The html as SafeHtml
   */
  getHtmlAndCss(projectWidget: ProjectWidget): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustHtml(`
      <style>
        ${projectWidget.widget.cssContent}
        ${projectWidget.customStyle}
      </style>
      ${projectWidget.instantiateHtml}
    `);
  }

  /**
   * Get the oommon css for each widget
   *
   * @returns {SafeHtml} AS safe HTML
   */
  getWidgetCommonCSS(): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustHtml(`
      <style>
        .grid-item h1 {
            margin-bottom: 12px;
            text-align: center;
            font-size: 1em;
            font-weight: 400;
            margin-right: 10px;
            margin-left: 10px;
          }
          .grid-item h2 {
            text-transform: uppercase;
            font-size: 3em;
            font-weight: 700;
            color: #fff;
          }
          .grid .widget a {
            text-decoration: none;
          }
          .grid-item p {
            padding: 0;
            margin: 0;
          }
          .grid-item .more-info {
            color: rgba(255, 255, 255, 0.5);
            font-size: 0.6em;
            position: absolute;
            bottom: 32px;
            left: 0;
            right: 0;
          }
          .grid-item .updated-at {
            font-size: 15px;
            position: absolute;
            bottom: 12px;
            left: 0;
            right: 0;
            color: rgba(0, 0, 0, 0.3);
          }
          .grid-item > div {
            position: relative;
            top: 50%;
            transform: translateY(-50%);
          }
        </style>
    `);
  }

  /**
   * Called when the component is getting destroy
   */
  ngOnDestroy() {
    this.isAlive = false;

    this.unsubscribeToWebsockets();
  }
}
