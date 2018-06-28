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

import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {MatDialog, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../dashboard.service';
import {switchMap} from 'rxjs/operators/switchMap';
import {catchError} from 'rxjs/operators';
import {of as observableOf} from 'rxjs/observable/of';
import {merge} from 'rxjs/observable/merge';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {ToastType} from '../../../shared/model/toastNotification/ToastType';
import {User} from '../../../shared/model/dto/user/User';
import {ToastService} from '../../../shared/components/toast/toast.service';
import {DeleteDashboardDialogComponent} from '../components/delete-dashboard-dialog/delete-dashboard-dialog.component';

/**
 * Component that manage the dashboard list for admin part
 */
@Component({
  selector: 'app-dashboard-list',
  templateUrl: './dashboard-list.component.html',
  styleUrls: ['./dashboard-list.component.css']
})
export class DashboardListComponent implements AfterViewInit {

  /**
   * Management of the table sorting
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * Management of the table pagination
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * The object that hold data management
   * @type {MatTableDataSource<User>}  The mat table of users
   */
  matTableDataSource = new MatTableDataSource<Project>();

  /**
   * Column displayed on the mat table
   * @type {string[]} The list of column references
   */
  displayedColumns = ['name', 'token', 'edit', 'delete'];
  /**
   * Management of the spinner
   * @type {boolean} True when we are loading result, false otherwise
   */
  isLoadingResults = false;
  /**
   * If we have an error while displaying the table
   * @type {boolean}
   */
  errorCatched = false;
  /**
   * The number of projects
   * @type {number}
   */
  resultsLength = 0;

  /**
   * Constructor
   *
   * @param {DashboardService} _dashboardService The dashboardService to inject
   * @param {ChangeDetectorRef} _changeDetectorRef The change detector ref
   * @param {MatDialog} _matDialog The matDialog service to inject
   * @param {ToastService} _toastService The toast service to inject
   */
  constructor(private _dashboardService: DashboardService,
              private _changeDetectorRef: ChangeDetectorRef,
              private _matDialog: MatDialog,
              private _toastService: ToastService) {
  }

  /**
   * Called when the view has been init
   */
  ngAfterViewInit() {
    this.initProjectsTable();
  }

  /**
   * Display every user inside the table
   */
  initProjectsTable(): void {
    // If the user changes the sort order, reset back to the first page.
    merge(this.matSort.sortChange, this.matPaginator.page)
        .pipe(
            startWith(null),
            switchMap(() => {
              this.isLoadingResults = true;
              this._changeDetectorRef.detectChanges();
              return this._dashboardService.getAll();
            }),
            map(data => {
              this.isLoadingResults = false;
              this.errorCatched = false;

              return data;
            }),
            catchError(() => {
              this.isLoadingResults = false;
              this.errorCatched = true;

              return observableOf([]);
            })
        )
        .subscribe(data => {
          this.resultsLength = data.length;
          this.matTableDataSource.data = data;
          this.matTableDataSource.sort = this.matSort;
        });
  }

  /**
   * Delete a created dashboard
   *
   * @param {Project} project The dashboard to delete
   */
  openDialogDeleteDashboard(project: Project) {
    const deleteUserDialogRef = this._matDialog.open(DeleteDashboardDialogComponent, {
      data: {project: project}
    });

    deleteUserDialogRef.afterClosed().subscribe(shouldDeleteDashboard => {
      if (shouldDeleteDashboard) {
        this
            ._dashboardService
            .deleteProject(project)
            .subscribe(() => {
              this._toastService.sendMessage('Project deleted successfully', ToastType.SUCCESS);
              this.initProjectsTable();
            });
      }
    });
  }

}
