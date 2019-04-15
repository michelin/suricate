/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {takeWhile} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import * as Stomp from '@stomp/stompjs';

import {Project} from '../../../../shared/model/api/project/Project';
import {WSUpdateEvent} from '../../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../../shared/model/websocket/enums/WSUpdateType';
import {SettingsService} from '../../../settings/settings.service';
import {SidenavService} from '../../../../layout/sidenav/sidenav.service';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {WebsocketService} from '../../../../shared/services/websocket.service';
import {UserService} from '../../../security/user/user.service';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {DashboardService} from '../../dashboard.service';

/**
 * Dashboard TV Management
 */
@Component({
  selector: 'app-code-view',
  templateUrl: './dashboard-tv.component.html',
  styleUrls: ['./dashboard-tv.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DashboardTvComponent implements OnInit, OnDestroy {

  /**
   * Tell if the component is displayed
   * @type {boolean}
   * @private
   */
  private isAlive = true;

  /**
   * The project
   * @type {Project}
   */
  project: Project;

  /**
   * The list of project widgets related to the project
   * @type {ProjectWidget[]}
   */
  projectWidgets: ProjectWidget[];

  /**
   * The screen code to display
   * @type {number}
   */
  screenCode: number;

  /**
   * The stompJS connection event subscription
   * @type {Subscription}
   */
  connectionEventSubscription: Subscription;

  /**
   * The constructor
   *
   * @param settingsService The setting service
   * @param sidenavService The sidenav service
   * @param activatedRoute The activated route
   * @param router The router service
   * @param httpProjectService The http service for project management
   * @param websocketService The websocket management
   * @param userService The user service
   * @param dashboardService The dashboard service
   */
  constructor(private settingsService: SettingsService,
              private sidenavService: SidenavService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private httpProjectService: HttpProjectService,
              private websocketService: WebsocketService,
              private userService: UserService,
              private dashboardService: DashboardService) {
  }

  /**********************************************************************************************************/
  /*                      COMPONENT LIFE CYCLE                                                              */

  /**********************************************************************************************************/

  /**
   * Init of the component
   */
  ngOnInit() {
    this.initDefaultScreenSettings();
    this.screenCode = this.websocketService.getscreenCode();
    this.listenForConnection();
    this.retrieveProjectTokenFromURL();

    this.dashboardService.refreshProjectEvent().subscribe(shouldRefresh => {
      if (shouldRefresh) {
        this.refreshProject(this.project.token);
      }
    });

    this.dashboardService.refreshProjectWidgetsEvent().subscribe(shouldRefresh => {
      if (shouldRefresh) {
        this.refreshProjectWidgets(this.project.token);
      }
    });
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.settingsService.initUserThemeSetting(this.userService.connectedUser);
    this.sidenavService.openSidenav();
    this.isAlive = false;
    this.disconnectTV();
  }

  /**
   * Init the settings to be in TV Mode
   */
  initDefaultScreenSettings() {
    setTimeout(() => this.settingsService.currentTheme = 'dark-theme', 500);
    this.sidenavService.closeSidenav();
  }

  /**
   * Get the project informations from the url query params
   */
  retrieveProjectTokenFromURL() {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['token']) {
        this.refreshProject(params['token']);

      } else {
        this.project = null;
      }
    });
  }

  /**
   * Refresh the project widget list
   */
  refreshProjectWidgets(dashboardToken: string): void {
    this.httpProjectService.getProjectProjectWidgets(dashboardToken).subscribe(projectWidgets => {
      this.projectWidgets = projectWidgets;
    });
  }

  /**
   * Refresh the project widget list
   */
  refreshProject(projectToken: string): void {
    this.httpProjectService.getOneByToken(projectToken).subscribe(project => {
      this.project = project;
      this.refreshProjectWidgets(projectToken);
    });
  }

  /**********************************************************************************************************/
  /*                      WEBSOCKET MANAGEMENT                                                              */

  /**********************************************************************************************************/

  /**
   * When on code view screen we wait for new connection
   */
  listenForConnection() {
    this.isAlive = true;
    this.websocketService.startConnection();

    const waitingConnectionUrl = `/user/${this.screenCode}/queue/connect`;
    this.connectionEventSubscription = this.websocketService.subscribeToDestination(waitingConnectionUrl).pipe(
      takeWhile(() => this.isAlive)
    ).subscribe((stompMessage: Stomp.Message) => {
      const updateEvent: WSUpdateEvent = JSON.parse(stompMessage.body);

      if (updateEvent.type === WSUpdateType.CONNECT) {
        const project: Project = updateEvent.content;
        if (project) {
          this.router.navigate(['/tv'], {queryParams: {token: project.token}});
        }
      }
    });
  }

  unsubscribeToConnectionEvent() {
    if (this.connectionEventSubscription) {
      this.connectionEventSubscription.unsubscribe();
      this.connectionEventSubscription = null;
    }

    this.isAlive = false;
  }

  /**
   * Disconnect TV from stompJS
   */
  disconnectTV() {
    this.unsubscribeToConnectionEvent();
    this.websocketService.disconnect();
  }

  /**
   * Handle the disconnection of a dashboard
   */
  handlingDashboardDisconnect() {
    this.router.navigate(['/tv']);
    setTimeout(() => this.listenForConnection(), 500);
  }
}
