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
import {merge, of as observableOf} from 'rxjs';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {TitleCasePipe} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

import {Project} from '../../../../../shared/model/api/Project';
import {DashboardService} from '../../../dashboard.service';
import {ToastService} from '../../../../../shared/components/toast/toast.service';
import {ConfirmDialogComponent} from '../../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {ToastType} from '../../../../../shared/components/toast/toast-objects/ToastType';
import {HttpProjectService} from '../../../../../shared/services/http/http-project.service';

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
   * @param {HttpProjectService} httpProjectService The http project service to inject
   * @param {DashboardService} dashboardService The dashboardService to inject
   * @param {TranslateService} translateService The translate service to inject
   * @param {ChangeDetectorRef} changeDetectorRef The change detector ref
   * @param {MatDialog} matDialog The matDialog service to inject
   * @param {ToastService} toastService The toast service to inject
   */
  constructor(private httpProjectService: HttpProjectService,
              private dashboardService: DashboardService,
              private translateService: TranslateService,
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
          return this.httpProjectService.getAll();
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
    let deleteUserDialog = null;

    this.translateService.get(['dashboard.delete', 'delete.confirm']).subscribe(translations => {
      const titleCasePipe = new TitleCasePipe();

      deleteUserDialog = this.matDialog.open(ConfirmDialogComponent, {
        data: {
          title: translations['dashboard.delete'],
          message: `${translations['delete.confirm']} ${titleCasePipe.transform(project.name)}`
        }
      });
    });

    deleteUserDialog.afterClosed().subscribe(shouldDeleteDashboard => {
      if (shouldDeleteDashboard) {
        this.httpProjectService.deleteProject(project).subscribe(() => {
          this.toastService.sendMessage('Project deleted successfully', ToastType.SUCCESS);
          this.initProjectsTable();
        });
      }
    });
  }

}
