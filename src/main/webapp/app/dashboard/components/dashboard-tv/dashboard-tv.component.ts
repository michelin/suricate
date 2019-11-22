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

import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { flatMap, takeWhile, tap } from 'rxjs/operators';
import { Observable, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import * as Stomp from '@stomp/stompjs';

import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { SettingsService } from '../../../core/services/settings.service';
import { SidenavService } from '../../../shared/services/frontend/sidenav.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { DashboardService } from '../../services/dashboard.service';
import { AuthenticationService } from '../../../shared/services/frontend/authentication.service';

/**
 * Dashboard TV Management
 */
@Component({
  selector: 'suricate-code-view',
  templateUrl: './dashboard-tv.component.html',
  styleUrls: ['./dashboard-tv.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DashboardTvComponent implements OnInit, OnDestroy {
  /**
   * The project token in the url
   * @type {string}
   * @protected
   */
  protected projectToken: string;

  /**
   * True if the screen is loading
   * @type {boolean}
   * @protected
   */
  protected isDashboardLoading = false;
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
   * @protected
   */
  protected screenCode = DashboardService.generateScreenCode();

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
  constructor(
    private settingsService: SettingsService,
    private sidenavService: SidenavService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private httpProjectService: HttpProjectService,
    private websocketService: WebsocketService,
    private dashboardService: DashboardService
  ) {
    this.projectToken = this.activatedRoute.snapshot.queryParams['token'];
    setTimeout(() => (this.settingsService.currentTheme = 'dark-theme'), 500);
  }

  /**********************************************************************************************************/
  /*                      COMPONENT LIFE CYCLE                                                              */

  /**********************************************************************************************************/

  /**
   * Init of the component
   */
  public ngOnInit(): void {
    this.listenForConnection();
    this.initComponent();
  }

  /**
   * Refresh the project displayed
   */
  private initComponent(): void {
    if (this.projectToken) {
      this.isDashboardLoading = true;

      this.refreshProject(this.projectToken)
        .pipe(flatMap(() => this.refreshProjectWidgets(this.projectToken)))
        .subscribe(
          () => {
            this.isDashboardLoading = false;
          },
          () => {
            this.isDashboardLoading = false;
          }
        );
    }
  }

  /**
   * Refresh the project widget list
   */
  private refreshProjectWidgets(dashboardToken: string): Observable<ProjectWidget[]> {
    return this.httpProjectService
      .getProjectProjectWidgets(dashboardToken)
      .pipe(tap((projectWidgets: ProjectWidget[]) => (this.projectWidgets = projectWidgets)));
  }

  /**
   * Refresh the project
   *
   * @param dashboardToken The token used for the refresh
   */
  private refreshProject(dashboardToken: string): Observable<Project> {
    return this.httpProjectService.getById(dashboardToken).pipe(tap((project: Project) => (this.project = project)));
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.settingsService.initUserThemeSetting(AuthenticationService.getConnectedUser());
    this.isAlive = false;
    this.disconnectTV();
  }

  /**
   * When on code view screen we wait for new connection
   */
  listenForConnection() {
    this.isAlive = true;
    this.websocketService.startConnection();

    const waitingConnectionUrl = `/user/${this.screenCode}/queue/connect`;
    this.connectionEventSubscription = this.websocketService
      .subscribeToDestination(waitingConnectionUrl)
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.CONNECT) {
          const project: Project = updateEvent.content;
          if (project) {
            this.router.navigate(['/tv'], { queryParams: { token: project.token } });
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
