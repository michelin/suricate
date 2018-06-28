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
import {ActivatedRoute} from '@angular/router';
import {DashboardService} from '../dashboard.service';
import {takeWhile} from 'rxjs/operators';
import {Project} from '../../../shared/model/dto/Project';
import {AddWidgetDialogComponent} from '../../../shared/components/pages-header/components/add-widget-dialog/add-widget-dialog.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';

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
   * The widget dialog ref
   */
  private _addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;

  /**
   * Tell if the component is displayed
   *
   * @type {boolean}
   */
  private _isAlive = true;

  /**
   * The project as observable
   */
  project$: Observable<Project>;

  /**
   * constructor
   *
   * @param {ActivatedRoute} _activatedRoute The activated route service
   * @param {DashboardService} _dashboardService The dashboard service
   * @param {MatDialog} _matDialog The mat dialog service
   */
  constructor(private _activatedRoute: ActivatedRoute,
              private _dashboardService: DashboardService,
              private _matDialog: MatDialog) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    // Global init from project
    this._activatedRoute.params.subscribe(params => {
      this._dashboardService
          .getOneById(+params['id'])
          .subscribe(project => {
            this._dashboardService.currentDisplayedDashboardValue = project;
          });
    });

    this._dashboardService.currentDisplayedDashboard$
        .pipe(takeWhile(() => this._isAlive))
        .subscribe(project => this.project$ = of(project));
  }

  /**
   * The add widget dialog ref
   */
  openAddWidgetDialog() {
    this._addWidgetDialogRef = this._matDialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      minHeight: 500,
    });
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this._isAlive = false;
  }

}
