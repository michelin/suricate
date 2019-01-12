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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';

import {DashboardService} from '../../dashboard.service';
import {Project} from '../../../../shared/model/api/project/Project';
import {AddWidgetDialogComponent} from '../../../../layout/header/components/add-widget-dialog/add-widget-dialog.component';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {WebsocketService} from '../../../../shared/services/websocket.service';

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
   * The list of projectWidgets
   */
  projectWidgets: ProjectWidget[];

  /**
   * The screen code of the client;
   */
  screenCode: number;

  /**
   * constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {HttpProjectService} httpProjectService The http project service
   * @param {MatDialog} matDialog The mat dialog service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private dashboardService: DashboardService,
              private websocketService: WebsocketService,
              private httpProjectService: HttpProjectService,
              private matDialog: MatDialog,
              private router: Router) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.screenCode = this.websocketService.getscreenCode();

    this.dashboardService.refreshProjectEvent().subscribe(shouldRefresh => {
      if (shouldRefresh) {
        this.refreshProject(this.project.token);
      }
    });

    // Global init from project
    this.activatedRoute.params.subscribe(params => {
      this.refreshProject(params['dashboardToken']);
    });
  }

  /**
   * Refresh the project
   */
  refreshProject(dashboardToken: string): void {
    this.httpProjectService.getOneByToken(dashboardToken).subscribe(project => {
      this.project = project;

      this.httpProjectService.getProjectProjectWidgets(this.project.token).subscribe(projectWidgets => {
        this.projectWidgets = projectWidgets;
      });
    });
  }

  /**
   * The add widget dialog ref
   */
  openAddWidgetDialog() {
    this.matDialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      minHeight: 500,
      data: {projectToken: this.project.token}
    });
  }

  /**
   * Handle the disconnection of a dashboard
   */
  handlingDashboardDisconnect() {
    this.router.navigate(['/home']);
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }

}
