/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { flatMap, takeUntil, tap } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { ActivatedRoute, Params, Router } from '@angular/router';
import * as Stomp from '@stomp/stompjs';
import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { DashboardScreenComponent } from '../dashboard-screen/dashboard-screen.component';
import { HttpProjectWidgetService } from '../../../shared/services/backend/http-project-widget/http-project-widget.service';

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
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * All project widgets, split by grid
   */
  public projectWidgetsByGrid = new Map<number, ProjectWidget[]>();

  /**
   * The rotation index
   */
  public rotationIndex = 0;

  /**
   * The screen code to display
   */
  public screenCode = DashboardService.generateScreenCode();

  /**
   * True if the screen is loading
   */
  public isDashboardLoading = false;

  /**
   * The project
   */
  public project: Project;

  /**
   * The returned object of the setInterval rotation
   */
  private intervalRotationTimer;

  /**
   * The constructor
   *
   * @param router              Service used to manage app's route
   * @param activatedRoute      Service used to manage the route activated by the component
   * @param httpProjectService  Service used to manage http calls for a project
   * @param httpProjectWidgetsService  The HTTP project widgets service
   * @param websocketService    Service used to manage websocket
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpProjectWidgetsService: HttpProjectWidgetService,
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

    this.activatedRoute.queryParams.pipe(takeUntil(this.unsubscribe)).subscribe((queryParams: Params) => {
      if (queryParams['token']) {
        this.initComponentWithProject(queryParams['token']).subscribe();
      } else {
        this.project = null;
      }
    });
  }

  /**
   * When the component is destroyed
   */
  public ngOnDestroy(): void {
    clearInterval(this.intervalRotationTimer);
    this.disconnectTV();
  }

  /**
   * Subscribe to websocket used to wait for new connections request
   */
  private listenForConnection(): void {
    const waitingConnectionUrl = `/user/${this.screenCode}/queue/connect`;

    this.websocketService.startConnection();

    this.websocketService
      .watch(waitingConnectionUrl)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        // Received when synchronizing to a single dashboard
        if (updateEvent.type === WebsocketUpdateTypeEnum.CONNECT_DASHBOARD) {
          const project: Project = updateEvent.content;
          if (project) {
            this.router.navigate(['/tv'], { queryParams: { token: project.token } });
          }
        }
      });
  }

  /**
   * Initialise the component from the given project token
   */
  public initComponentWithProject(projectToken: string): Observable<ProjectWidget[]> {
    this.isDashboardLoading = true;

    return this.refreshProject(projectToken)
      .pipe(flatMap(() => this.refreshProjectWidgets(projectToken)))
      .pipe(
        tap(
          () => (this.isDashboardLoading = false),
          () => (this.isDashboardLoading = false)
        )
      );
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
  public refreshAllProjectWidgets(): void {
    this.refreshProjectWidgets(this.project.token).subscribe();
  }

  /**
   * Refresh the project widget list
   *
   * @param dashboardToken The token used for the refresh
   */
  private refreshProjectWidgets(dashboardToken: string): Observable<ProjectWidget[]> {
    return this.httpProjectWidgetsService.getAllByProjectToken(dashboardToken).pipe(
      tap((projectWidgets: ProjectWidget[]) => {
        this.project.grids.forEach(projectGrid => {
          this.projectWidgetsByGrid.set(
            projectGrid.id,
            projectWidgets.filter(projectWidget => projectWidget.gridId === projectGrid.id)
          );
        });

        this.scheduleRotation();
      })
    );
  }

  /**
   * Schedule the next rotation of dashboards
   */
  private scheduleRotation(): void {
    clearInterval(this.intervalRotationTimer);

    this.intervalRotationTimer = setInterval(() => {
      this.rotationIndex = this.rotationIndex === this.project.grids.length - 1 ? 0 : this.rotationIndex + 1;
    }, this.project.grids[this.rotationIndex].time * 1000);
  }

  /**
   * Handle the disconnection of a dashboard
   */
  public handlingDashboardDisconnect(): void {
    this.router.navigate(['/tv']);
    setTimeout(() => this.listenForConnection(), 500);
  }

  /**
   * Disconnect TV from stompJS
   */
  private disconnectTV(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();

    this.websocketService.disconnect();
  }
}
