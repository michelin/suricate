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

import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import * as html2canvas from 'html2canvas';

import {DashboardService} from '../../dashboard.service';
import {Project} from '../../../../shared/model/api/project/Project';
import {AddWidgetDialogComponent} from '../../../../layout/header/components/add-widget-dialog/add-widget-dialog.component';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {WebsocketService} from '../../../../shared/services/websocket.service';
import {ImageUtils} from '../../../../shared/utils/ImageUtils';
import {FileUtils} from '../../../../shared/utils/FileUtils';

/**
 * Component that display a specific dashboard
 */
@Component({
  selector: 'app-dashboard-detail',
  templateUrl: './dashboard-detail.component.html',
  styleUrls: ['./dashboard-detail.component.scss']
})
export class DashboardDetailComponent implements OnInit, OnDestroy {

  /**
   * Tell if the component is displayed
   * @type {boolean}
   * @private
   */
  private isAlive = true;

  /**
   * The dashboard html (as HTML Element)
   */
  @ViewChild('dashboardScreen') dashboardScreen: ElementRef;

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
   * True if the dashboard should be displayed readonly, false otherwise
   */
  isReadOnly: boolean = true;

  /**
   * The screen code of the client;
   */
  screenCode: number;

  imgtest: any;

  /**
   * constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {WebsocketService} websocketService The websocket service
   * @param {HttpProjectService} httpProjectService The http project service
   * @param {MatDialog} matDialog The mat dialog service
   * @param {Router} router The router service on Angular
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

    this.dashboardService.refreshProjectWidgetsEvent().subscribe(shouldRefresh => {
      if (shouldRefresh) {
        this.refreshProjectWidgets(this.project.token);
      }
    });

    // Global init from project
    this.activatedRoute.params.subscribe(params => {
      this.refreshProject(params['dashboardToken']);
    });

    setTimeout(() => this.takeDashboardScreenshot(), 20000);
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
   * Refresh the project
   */
  refreshProject(dashboardToken: string): void {
    this.httpProjectService.getOneByToken(dashboardToken).subscribe(project => {
      this.project = project;
      this.refreshReadOnlyDashboard(dashboardToken);
      this.refreshProjectWidgets(dashboardToken);
    });
  }

  /**
   * Check if the dashboard should be displayed as readonly
   *
   * @param dashboardToken
   */
  refreshReadOnlyDashboard(dashboardToken: string): void {
    this.dashboardService.shouldDisplayedReadOnly(dashboardToken).subscribe(shouldDisplayReadOnly => {
      this.isReadOnly = shouldDisplayReadOnly;
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
   * Take screenshot of dashboard
   */
  takeDashboardScreenshot() {
    html2canvas(document.getElementById('dashboardScreen')).then(canvas => {
      const imgUrl = canvas.toDataURL('image/png');

      const blob: Blob = FileUtils.base64ToBlob(ImageUtils.getDataFromBase64URL(imgUrl), ImageUtils.getContentTypeFromBase64URL(imgUrl));
      const imageFile: File = FileUtils.convertBlobToFile(blob, `${this.project.token}.png`, new Date());

      this.httpProjectService.addOrUpdateProjectScreenshot(this.project.token, imageFile).subscribe();
    });
  }

  /**
   * Handle the disconnection of a dashboard
   */
  handlingDashboardDisconnect() {
    this.httpProjectService.getAllForCurrentUser().subscribe((projects: Project[]) => {
      this.dashboardService.currentDashboardListValues = projects;
    });
    this.router.navigate(['/home']);
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }

}
