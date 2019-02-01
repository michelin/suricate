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

import {Component, OnInit} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ActivatedRoute} from '@angular/router';

import {AddWidgetDialogComponent} from '../add-widget-dialog/add-widget-dialog.component';
import {AddDashboardDialogComponent} from '../../../../home/components/add-dashboard-dialog/add-dashboard-dialog.component';
import {TvManagementDialogComponent} from '../tv-management-dialog/tv-management-dialog.component';
import {HttpScreenService} from '../../../../shared/services/api/http-screen.service';
import {Project} from '../../../../shared/model/api/project/Project';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';

/**
 * Hold the header dashboard actions
 */
@Component({
  selector: 'app-dashboard-actions',
  templateUrl: './dashboard-actions.component.html',
  styleUrls: ['./dashboard-actions.component.scss']
})
export class DashboardActionsComponent implements OnInit {

  /**
   * Dialog reference used for add a widget
   * @type {MatDialogRef<AddWidgetDialogComponent>}
   */
  addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;

  /**
   * Dialog reference used for edit a dashboard
   * @type {MatDialogRef<AddDashboardDialogComponent>}
   */
  editDashboardDialogRef: MatDialogRef<AddDashboardDialogComponent>;

  /**
   * Dialog reference used for TV Management
   * @type {MatDialogRef<TvManagementDialogComponent>}
   */
  tvManagementDialogRef: MatDialogRef<TvManagementDialogComponent>;

  /**
   * The current project id
   * @type {Project}
   */
  project: Project;

  /**
   * The constructor
   *
   * @param {MatDialog} matDialog The mat dialog to inject
   * @param {ActivatedRoute} activatedRoute The activated route
   * @param {HttpScreenService} httpScreenService The screen service
   * @param {HttpProjectService} httpProjectService The project service
   */
  constructor(private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private httpScreenService: HttpScreenService,
              private httpProjectService: HttpProjectService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.httpProjectService.getOneByToken(params['dashboardToken']).subscribe(dashboard => {
        this.project = dashboard;
      });
    });
  }

  /**
   * Open the Add widget dialog
   */
  openAddWidgetDialog() {
    this.addWidgetDialogRef = this.matDialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      data: {projectToken: this.project.token}
    });
  }

  /**
   * Open the edit widget dialog
   */
  openEditDashboardDialog() {
    this.editDashboardDialogRef = this.matDialog.open(AddDashboardDialogComponent, {
      minWidth: 900,
      data: {projectToken: this.project.token}
    });
  }

  /**
   * Open the tv management dialog
   */
  openTvManagementDialog() {
    this.tvManagementDialogRef = this.matDialog.open(TvManagementDialogComponent, {
      minWidth: 900,
      data: {projectToken: this.project.token}
    });
  }

  /**
   * Refresh every screens for the current dashboard
   */
  refreshConnectedScreens() {
    this.httpScreenService.refreshEveryConnectedScreensForProject(this.project.token).subscribe();
  }
}
