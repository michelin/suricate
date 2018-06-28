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
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../../dashboard/dashboard.service';
import {takeWhile} from 'rxjs/operators';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AddDashboardDialogComponent} from '../components/add-dashboard-dialog/add-dashboard-dialog.component';
import {Router} from '@angular/router';

/**
 * Manage the home page
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  /**
   * True while the component is instantiate
   * @type {boolean}
   */
  private _isAlive = true;

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
   * @param {DashboardService} _dashboardService The dashboard service
   * @param {MatDialog} _matDialog The mat dialog service
   * @param {Router} _router The router service
   */
  constructor(private _dashboardService: DashboardService,
              private _matDialog: MatDialog,
              private _router: Router) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this._dashboardService.currentDashboardList$
        .pipe(takeWhile(() => this._isAlive))
        .subscribe(dashboards => this.dashboards = dashboards);
  }

  /**
   * Open the add dashboard dialog
   */
  openAddDashboardDialog() {
    this.addDashboardDialogRef = this._matDialog.open(AddDashboardDialogComponent, {
      minWidth: 900,
      minHeight: 500,
    });
  }

  /**
   * Navigate to a dashboard
   *
   * @param {number} dashboardId The dashboard id
   */
  navigateToDashboard(dashboardId: number) {
    this._router.navigate(['/dashboard', dashboardId]);
  }

  /**
   * Called when the component is destroy
   */
  ngOnDestroy() {
    this._isAlive = false;
  }

}
