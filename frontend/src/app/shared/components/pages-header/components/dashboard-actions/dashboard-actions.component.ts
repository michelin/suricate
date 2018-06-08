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
import {AddWidgetDialogComponent} from '../add-widget-dialog/add-widget-dialog.component';
import {ActivatedRoute} from '@angular/router';
import {AddDashboardDialogComponent} from '../../../../../modules/home/components/add-dashboard-dialog/add-dashboard-dialog.component';
import {TvManagementDialogComponent} from '../tv-management-dialog/tv-management-dialog.component';
import {ScreenService} from '../../../../../modules/dashboard/screen.service';
import {DashboardService} from '../../../../../modules/dashboard/dashboard.service';
import {Project} from '../../../../model/dto/Project';

@Component({
  selector: 'app-dashboard-actions',
  templateUrl: './dashboard-actions.component.html',
  styleUrls: ['./dashboard-actions.component.css']
})
export class DashboardActionsComponent implements OnInit {

  /**
   * Dialog reference used for add a widget
   */
  addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;

  /**
   * Dialog reference used for edit a dashboard
   */
  editDashboardDialogRef: MatDialogRef<AddDashboardDialogComponent>;

  /**
   * Dialog reference used for TV Management
   */
  tvManagementDialogRef: MatDialogRef<TvManagementDialogComponent>;

  /**
   * The current project id
   */
  project: Project;

  /**
   * The constructor
   *
   * @param {MatDialog} dialog The mat dialog to inject
   * @param {ActivatedRoute} activatedRoute The activated route
   * @param {ScreenService} screenService The screen service
   * @param {DashboardService} dashboardService The dashboard service
   */
  constructor(private dialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private screenService: ScreenService,
              private dashboardService: DashboardService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.dashboardService.currendDashbordSubject.subscribe(project => this.project = project);
  }

  /**
   * Open the Add widget dialog
   */
  openAddWidgetDialog() {
    this.addWidgetDialogRef = this.dialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      data: {projectId: this.project.id}
    });
  }

  /**
   * Open the edit widget dialog
   */
  openEditDashboardDialog() {
    this.editDashboardDialogRef = this.dialog.open(AddDashboardDialogComponent, {
      minWidth: 900,
      data: {projectId: this.project.id}
    });
  }

  /**
   * Open the tv management dialog
   */
  openTvManagementDialog() {
    this.tvManagementDialogRef = this.dialog.open(TvManagementDialogComponent, {
      minWidth: 900,
      data: {projectId: this.project.id}
    });
  }

  /**
   * Refresh every screens for the current dashboard
   */
  refreshConnectedScreens() {
    this.screenService.refreshEveryConnectedScreensForProject(this.project.token);
  }
}
