/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { KeyValuePipe, NgOptimizedImage } from '@angular/common';
import { Component, inject, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { IMessage } from '@stomp/rx-stomp';
import { Observable, Subject } from 'rxjs';
import { mergeMap, takeUntil, tap } from 'rxjs/operators';

import { ProgressBar } from '../../../shared/components/progress-bar/progress-bar';
import { Spinner } from '../../../shared/components/spinner/spinner';
import { HideAfterInitDirective } from '../../../shared/directives/hide-after-init/hide-after-init-directive';
import { WebsocketUpdateType } from '../../../shared/enums/websocket-update-type';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project-service';
import { HttpProjectWidgetService } from '../../../shared/services/backend/http-project-widget/http-project-widget-service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket-service';
import { DashboardService } from '../../services/dashboard/dashboard-service';
import { DashboardScreen } from '../dashboard-screen/dashboard-screen';

/**
 * Dashboard TV Management
 */
@Component({
	selector: 'suricate-code-view',
	templateUrl: './dashboard-tv.html',
	styleUrls: ['./dashboard-tv.scss'],
	encapsulation: ViewEncapsulation.None,
	imports: [NgOptimizedImage, Spinner, DashboardScreen, HideAfterInitDirective, ProgressBar, KeyValuePipe]
})
export class DashboardTv implements OnInit, OnDestroy {
	private readonly router = inject(Router);
	private readonly activatedRoute = inject(ActivatedRoute);
	private readonly httpProjectService = inject(HttpProjectService);
	private readonly httpProjectWidgetsService = inject(HttpProjectWidgetService);
	private readonly websocketService = inject(WebsocketService);

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
	private rotationInterval: NodeJS.Timeout;

	/**
	 * Time in percent for progress bar
	 */
	public timer = 0;
	public timerPercentage = 100;
	public timerInterval: NodeJS.Timeout;

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
				this.resetRotation();
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
			.subscribe((stompMessage: IMessage) => {
				const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

				// Received when synchronizing to a single dashboard
				if (updateEvent.type === WebsocketUpdateType.CONNECT_DASHBOARD) {
					const project: Project = updateEvent.content as Project;
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
			.pipe(mergeMap(() => this.refreshProjectWidgets(projectToken)))
			.pipe(
				tap({
					next: () => (this.isDashboardLoading = false),
					error: () => (this.isDashboardLoading = false)
				})
			);
	}

	/**
	 * Activate the action of refresh current project and widgets
	 */
	public refreshCurrentProjectAndWidgets(): void {
		this.resetRotation();
		this.initComponentWithProject(this.project.token).subscribe();
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
	 * Refresh the project widget list
	 *
	 * @param dashboardToken The token used for the refresh
	 */
	private refreshProjectWidgets(dashboardToken: string): Observable<ProjectWidget[]> {
		return this.httpProjectWidgetsService.getAllByProjectToken(dashboardToken).pipe(
			tap((projectWidgets: ProjectWidget[]) => {
				this.project.grids.forEach((projectGrid) => {
					this.projectWidgetsByGrid.set(
						projectGrid.id,
						projectWidgets.filter((projectWidget) => projectWidget.gridId === projectGrid.id)
					);
				});

				this.scheduleRotation();
			})
		);
	}

	/**
	 * Start the timer
	 */
	private startTimer(): void {
		this.resetTimer();

		const intervalRefreshMs = 100;
		this.timerInterval = setInterval(() => {
			this.timer -= intervalRefreshMs;
			this.timerPercentage = (this.timer * 100) / (this.project.grids[this.rotationIndex].time * 1000);
		}, intervalRefreshMs);
	}

	/**
	 * Stop the timer
	 */
	private resetTimer(): void {
		clearInterval(this.timerInterval);
		this.timer = this.project.grids[this.rotationIndex].time * 1000;
		this.timerPercentage = 100;
	}

	/**
	 * Schedule the next rotation of dashboards
	 */
	private scheduleRotation(): void {
		if (this.project.grids.length > 1) {
			if (this.project.displayProgressBar) {
				this.startTimer();
			}

			this.rotationInterval = setInterval(() => {
				this.rotationIndex = this.rotationIndex === this.project.grids.length - 1 ? 0 : this.rotationIndex + 1;

				clearInterval(this.rotationInterval);
				this.scheduleRotation();
			}, this.project.grids[this.rotationIndex].time * 1000);
		}
	}

	/**
	 * Reset the current rotation
	 */
	private resetRotation(): void {
		this.rotationIndex = 0;
		this.projectWidgetsByGrid.clear();
		clearInterval(this.rotationInterval);
	}

	/**
	 * Handle the disconnection of a dashboard
	 */
	public handlingDashboardDisconnect(): void {
		this.router.navigate(['/tv']);
		setTimeout(() => this.listenForConnection(), 500);
	}

	/**
	 * Disconnect TV from RxStomp
	 */
	private disconnectTV(): void {
		this.unsubscribe.next();
		this.unsubscribe.complete();

		clearInterval(this.timerInterval);
		clearInterval(this.rotationInterval);

		this.websocketService.disconnect();
	}
}
