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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

import { Project } from '../../../shared/models/backend/project/project';
import { DashboardService } from '../../../dashboard/services/dashboard.service';
import { HttpAssetService } from '../../../shared/services/backend/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';

/**
 * Manage the home page
 */
@Component({
  selector: 'suricate-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  protected headerConfiguration: HeaderConfiguration;
  /**
   * True while the component is instantiate
   * @type {boolean}
   */
  private isAlive = true;

  /**
   * The list of dashboards
   * @type {Project[]}
   */
  dashboards: Project[];

  /**
   * The constructor
   *
   * @param {DashboardService} dashboardService The dashboard service
   * @param {HttpAssetService} httpAssetService The http asset service to inject
   * @param {MatDialog} matDialog The mat dialog service
   * @param {Router} router The router service
   */
  constructor(
    private dashboardService: DashboardService,
    private readonly httpProjectService: HttpProjectService,
    private httpAssetService: HttpAssetService,
    private matDialog: MatDialog,
    private router: Router
  ) {
    this.initHeaderConfiguration();
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.httpProjectService.getAllForCurrentUser().subscribe(dashboards => {
      this.dashboards = dashboards;
    });
  }

  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboards.my' };
  }

  /**
   * Navigate to a dashboard
   *
   * @param {string} projectToken The project token
   */
  navigateToDashboard(projectToken: string) {
    this.router.navigate(['/dashboards', projectToken]);
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset token
   */
  getContentUrl(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
  }

  /**
   * Called when the component is destroy
   */
  ngOnDestroy() {
    this.isAlive = false;
  }
}
