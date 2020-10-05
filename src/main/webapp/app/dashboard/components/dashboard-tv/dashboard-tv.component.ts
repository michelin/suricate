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
import { ActivatedRoute, Params, Router } from '@angular/router';
import * as Stomp from '@stomp/stompjs';

import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { DashboardService } from '../../services/dashboard/dashboard.service';

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
  public projectToken: string;
  /**
   * The list of project widgets related to the project token
   * @type {ProjectWidget[]}
   * @protected
   */
  public projectWidgets: ProjectWidget[];

  /**
   * The screen code to display
   * @type {number}
   * @protected
   */
  public screenCode = DashboardService.generateScreenCode();

  /**
   * True if the screen is loading
   * @type {boolean}
   * @protected
   */
  public isDashboardLoading = false;

  /**
   * Tell if the component is displayed
   * @type {boolean}
   * @private
   */
  private isAlive = true;

  /**
   * The stompJS connection event subscription
   * @type {Subscription}
   * @private
   */
  private connectionEventSubscription: Subscription;

  /**
   * The project
   * @type {Project}
   * @protected
   */
  public project: Project;

  /**
   * The constructor
   *
   * @param {Router} router Angular service used to manage app's route
   * @param {ActivatedRoute} activatedRoute Angular service used to manage the route activated by the component
   * @param {HttpProjectService} httpProjectService Suricate service used to manage http calls for a project
   * @param {WebsocketService} websocketService Frontend service used to manage websocket
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpProjectService: HttpProjectService,
    private readonly websocketService: WebsocketService
  ) {}

  /**********************************************************************************************************/
  /*                      COMPONENT LIFE CYCLE                                                              */

  /**********************************************************************************************************/

  /**
   * Called when the component is init for the first time
   */
  public ngOnInit(): void {
    this.listenForConnection();

    /* this.activatedRoute.queryParams.subscribe((queryParams: Params) => {
      if (queryParams['token']) {
        this.projectToken = queryParams['token'];
        this.initComponent();
      } else {
        this.projectToken = null;
        this.projectWidgets = null;
      }
    }); */
  }

  /**
   * Subscribe to websocket used to wait for new connections request
   */
  private listenForConnection(): void {
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

  /**
   * Initialise the component
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
   * Refresh the project
   *
   * @param dashboardToken The token used for the refresh
   */
  private refreshProject(dashboardToken: string): Observable<Project> {
    return this.httpProjectService.getById(dashboardToken).pipe(tap((project: Project) => (this.project = project)));
  }

  /**
   * Activate the action of refresh project widgets
   */
  public refreshProjectWidgetsAction(): void {
    this.refreshProjectWidgets(this.project.token).subscribe();
  }
  /**
   * Refresh the project widget list
   *
   * @param dashboardToken The token used for the refresh
   */
  private refreshProjectWidgets(dashboardToken: string): Observable<ProjectWidget[]> {
    return this.httpProjectService
      .getProjectProjectWidgets(dashboardToken)
      .pipe(tap((projectWidgets: ProjectWidget[]) => (this.projectWidgets = projectWidgets)));
  }

  /**
   * Handle the disconnection of a dashboard
   */
  public handlingDashboardDisconnect(): void {
    this.router.navigate(['/tv']);
    setTimeout(() => this.listenForConnection(), 500);
  }

  /**
   * When the component is destroyed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
    this.disconnectTV();
  }

  /**
   * Disconnect TV from stompJS
   */
  private disconnectTV(): void {
    this.unsubscribeToConnectionEvent();
    this.websocketService.disconnect();
  }

  /**
   * Used to unsubscribe to the websocket
   */
  private unsubscribeToConnectionEvent(): void {
    if (this.connectionEventSubscription) {
      this.connectionEventSubscription.unsubscribe();
      this.connectionEventSubscription = null;
    }
  }
}
