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
import {MatDialog, MatDialogRef} from '@angular/material';
import {Router} from '@angular/router';
import {takeWhile} from 'rxjs/operators';

import {Project} from '../shared/model/api/project/Project';
import {DashboardService} from '../modules/dashboard/dashboard.service';
import {AddDashboardDialogComponent} from './components/add-dashboard-dialog/add-dashboard-dialog.component';
import {HttpAssetService} from '../shared/services/api/http-asset.service';

/**
 * Manage the home page
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  /**
   * True while the component is instantiate
   * @type {boolean}
   */
  private isAlive = true;

  /**
   * The add dashboard dialog
   * @type {MatDialogRef<AddDashboardDialogComponent>}
   */
  addDashboardDialogRef: MatDialogRef<AddDashboardDialogComponent>;

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
  constructor(private dashboardService: DashboardService,
              private httpAssetService: HttpAssetService,
              private matDialog: MatDialog,
              private router: Router) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.dashboardService.currentDashboardList$.pipe(
      takeWhile(() => this.isAlive)
    ).subscribe(dashboards => {
      this.dashboards = dashboards;
    });
  }

  /**
   * Open the add dashboard dialog
   */
  openAddDashboardDialog() {
    this.addDashboardDialogRef = this.matDialog.open(AddDashboardDialogComponent, {
      minWidth: 900,
      minHeight: 500,
    });
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
