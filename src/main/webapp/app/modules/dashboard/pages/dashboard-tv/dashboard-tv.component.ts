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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {takeWhile} from 'rxjs/operators';

import {Project} from '../../../../shared/model/api/project/Project';
import {WSUpdateEvent} from '../../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../../shared/model/websocket/enums/WSUpdateType';

import * as Stomp from '@stomp/stompjs';
import {SettingsService} from '../../../../shared/services/settings.service';
import {SidenavService} from '../../../../layout/sidenav/sidenav.service';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {WebsocketService} from '../../../../shared/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../security/user/user.service';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {DashboardService} from '../../dashboard.service';

/**
 * Dashboard TV Management
 */
@Component({
  selector: 'app-code-view',
  templateUrl: './dashboard-tv.component.html',
  styleUrls: ['./dashboard-tv.component.css']
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
   * The constructor
   *
   * @param settingsService The setting service
   * @param sidenavService The sidenav service
   * @param activatedRoute The activated route
   * @param router The router service
   * @param httpProjectService The http service for project management
   * @param websocketService The websocket management
   * @param userService The user service
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

  /**
   * Init of the component
   */
  ngOnInit() {
    this.initDefaultScreenSettings();
    this.screenCode = this.websocketService.getscreenCode();
    this.listenForConnection();
    this.retrieveProjectTokenFromURL();

    this.dashboardService.refreshProjectWidgetListEvent().subscribe(shouldRefresh => {
      if (shouldRefresh) {
        this.refreshProjectWidgetList();
      }
    });
  }

  /**
   * Init the settings to be in TV Mode
   */
  initDefaultScreenSettings() {
    setTimeout(() => this.settingsService.currentTheme = 'dark-theme', 0);
    this.sidenavService.closeSidenav();
  }

  /**
   * When on code view screen we wait for new connection
   */
  listenForConnection() {
    this.websocketService.startConnection();

    const waitingConnectionUrl = `/user/${this.screenCode}/queue/connect`;
    this.websocketService.subscribeToDestination(waitingConnectionUrl).pipe(
      takeWhile(() => this.isAlive)
    ).subscribe((stompMessage: Stomp.Message) => {
      const updateEvent: WSUpdateEvent = JSON.parse(stompMessage.body);

      if (updateEvent.type === WSUpdateType.CONNECT) {
        const project: Project = updateEvent.content;
        if (project) {
          this.websocketService.disconnect();
          this.router.navigate(['/tv'], {queryParams: {token: project.token}});
        }
      }
    });
  }

  /**
   * Get the project informations from the url query params
   */
  retrieveProjectTokenFromURL() {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['token']) {
        this.httpProjectService.getOneByToken(params['token']).subscribe(project => {
          this.project = project;
          this.refreshProjectWidgetList();
        });
      }
    });
  }

  /**
   * Refresh the project widget list
   */
  refreshProjectWidgetList(): void {
    this.httpProjectService.getProjectProjectWidgets(this.project.token).subscribe(projectWidgets => {
      this.projectWidgets = projectWidgets;
    });
  }

  /**
   * Handle the disconnection of a dashboard
   */
  handlingDashboardDisconnect() {
    this.router.navigate(['/tv']);
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.settingsService.initUserThemeSetting(this.userService.connectedUser);
    this.isAlive = false;
    this.sidenavService.openSidenav();
    this.websocketService.disconnect();
  }
}
