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

import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { flatMap, takeUntil, tap } from 'rxjs/operators';
import { Observable, Subject, timer } from 'rxjs';
import { ActivatedRoute, Params, Router } from '@angular/router';
import * as Stomp from '@stomp/stompjs';
import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { HttpRotationService } from '../../../shared/services/backend/http-rotation/http-rotation.service';
import { Rotation } from '../../../shared/models/backend/rotation/rotation';
import { RotationProject } from '../../../shared/models/backend/rotation-project/rotation-project';

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
   * The list of project rotations
   */
  public rotationProjects: RotationProject[];

  /**
   * The list of project widgets related to the project token
   */
  public projectWidgets: ProjectWidget[];

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
   * The rotation
   */
  public rotation: Rotation;

  public timer;
  public timerPercentage;
  public timerTimeout: NodeJS.Timeout;

  /**
   * The constructor
   *
   * @param router              Service used to manage app's route
   * @param activatedRoute      Service used to manage the route activated by the component
   * @param httpProjectService  Service used to manage http calls for a project
   * @param httpRotationService Service used to manage http calls for a rotation
   * @param websocketService    Service used to manage websocket
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpRotationService: HttpRotationService,
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
      if (queryParams['dashboard']) {
        this.initComponentWithProject(queryParams['dashboard']).subscribe();
      } else if (queryParams['rotation']) {
        this.initComponentWithRotation(queryParams['rotation']);
      } else {
        this.rotation = this.project = this.projectWidgets = null;
      }
    });
  }

  /**
   * When the component is destroyed
   */
  public ngOnDestroy(): void {
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
        if (updateEvent.type === WebsocketUpdateTypeEnum.CONNECT_SINGLE_DASHBOARD) {
          const project: Project = updateEvent.content;
          if (project) {
            this.router.navigate(['/tv'], { queryParams: { dashboard: project.token } });
          }
        }

        // Received when synchronizing to a rotation
        if (updateEvent.type === WebsocketUpdateTypeEnum.CONNECT_ROTATION) {
          const rotation: Rotation = updateEvent.content;
          if (rotation) {
            this.router.navigate(['/tv'], { queryParams: { rotation: rotation.token } });
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
   * Initialise the component from the given rotation token
   */
  private initComponentWithRotation(rotationToken: string): void {
    this.isDashboardLoading = true;
    this.refreshRotation(rotationToken).subscribe(
      () => (this.isDashboardLoading = false),
      () => (this.isDashboardLoading = false)
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
   * Refresh the rotation
   */
  private refreshRotation(rotationToken: string): Observable<Rotation> {
    return this.httpRotationService.getById(rotationToken).pipe(tap((rotation: Rotation) => (this.rotation = rotation)));
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
    return this.httpProjectService
      .getWidgetInstancesByProjectToken(dashboardToken)
      .pipe(tap((projectWidgets: ProjectWidget[]) => (this.projectWidgets = projectWidgets)));
  }

  /**
   * Handle the disconnection of a dashboard
   */
  public handlingDashboardDisconnect(): void {
    this.stopRotationTimer();
    this.router.navigate(['/tv']);
    setTimeout(() => this.listenForConnection(), 500);
  }

  /**
   * Perform the rotation from the event received by the child component
   * through the web socket
   *
   * @param event The websocket event containing the next project to display
   */
  public performRotation(event: WebsocketUpdateEvent): void {
    this.stopRotationTimer();
    this.timerPercentage = 100;

    this.initComponentWithProject((event.content as Project).token).subscribe(() => {
      const timeBeforeNextRotation = (this.timer = (new Date(event.date).getTime() - new Date().getTime()) / 1000);
      this.timerTimeout = setInterval(() => {
        --this.timer;
        this.timerPercentage = (this.timer * 100) / timeBeforeNextRotation;
      }, 1000);
    });
  }

  /**
   * Stop the rotation timer
   */
  private stopRotationTimer(): void {
    if (this.timerTimeout) {
      clearInterval(this.timerTimeout);
    }
  }

  /**
   * Disconnect TV from stompJS
   */
  private disconnectTV(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();

    this.stopRotationTimer();

    this.websocketService.disconnect();
  }
}
