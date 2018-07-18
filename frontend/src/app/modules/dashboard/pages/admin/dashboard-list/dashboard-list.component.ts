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
import {merge} from 'rxjs/observable/merge';
import {of as observableOf} from 'rxjs/observable/of';
import {catchError} from 'rxjs/operators';
import {map} from 'rxjs/operators/map';
import {startWith} from 'rxjs/operators/startWith';
import {switchMap} from 'rxjs/operators/switchMap';

import {Project} from '../../../../../shared/model/dto/Project';
import {DashboardService} from '../../../dashboard.service';
import {ToastType} from '../../../../../shared/model/toastNotification/ToastType';
import {User} from '../../../../../shared/model/dto/user/User';
import {ToastService} from '../../../../../shared/components/toast/toast.service';
import {DeleteDashboardDialogComponent} from '../../../components/delete-dashboard-dialog/delete-dashboard-dialog.component';

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
   * @type {MatSort}
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * Management of the table pagination
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * The object that hold data management
   * @type {MatTableDataSource<User>}
   */
  matTableDataSource = new MatTableDataSource<Project>();

  /**
   * Column displayed on the mat table
   * @type {string[]}
   */
  displayedColumns = ['name', 'token', 'edit', 'delete'];
  /**
   * Management of the spinner
   * @type {boolean}
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
   * @param {DashboardService} dashboardService The dashboardService to inject
   * @param {ChangeDetectorRef} changeDetectorRef The change detector ref
   * @param {MatDialog} matDialog The matDialog service to inject
   * @param {ToastService} toastService The toast service to inject
   */
  constructor(private dashboardService: DashboardService,
              private changeDetectorRef: ChangeDetectorRef,
              private matDialog: MatDialog,
              private toastService: ToastService) {
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
              this.changeDetectorRef.detectChanges();
              return this.dashboardService.getAll();
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

    // Apply sort custom rules for dashboards
    this.matTableDataSource.sortingDataAccessor = (dashboard: Project, property: string) => {
      switch (property) {
        case 'name':
          return dashboard.name.toLocaleLowerCase();
        default:
          return dashboard[property];
      }
    };
  }

  /**
   * Delete a created dashboard
   *
   * @param {Project} project The dashboard to delete
   */
  openDialogDeleteDashboard(project: Project) {
    const deleteUserDialogRef = this.matDialog.open(DeleteDashboardDialogComponent, {
      data: {project: project}
    });

    deleteUserDialogRef.afterClosed().subscribe(shouldDeleteDashboard => {
      if (shouldDeleteDashboard) {
        this
            .dashboardService
            .deleteProject(project)
            .subscribe(() => {
              this.toastService.sendMessage('Project deleted successfully', ToastType.SUCCESS);
              this.initProjectsTable();
            });
      }
    });
  }

}
