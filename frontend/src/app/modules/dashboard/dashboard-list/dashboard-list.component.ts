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

import {ChangeDetectorRef, Component, ViewChild, AfterViewInit} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../dashboard.service';
import {switchMap} from 'rxjs/operators/switchMap';
import {catchError} from 'rxjs/operators';
import {of as observableOf} from 'rxjs/observable/of';
import {merge} from 'rxjs/observable/merge';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';

@Component({
  selector: 'app-dashboard-list',
  templateUrl: './dashboard-list.component.html',
  styleUrls: ['./dashboard-list.component.css']
})
export class DashboardListComponent implements AfterViewInit {

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
   * Management of the table sorting
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * Management of the table pagination
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * Constructor
   *
   * @param {DashboardService} dashboardService The dashboardService to inject
   * @param {ChangeDetectorRef} changeDetectorRef The change detector ref
   */
  constructor(private dashboardService: DashboardService,
              private changeDetectorRef: ChangeDetectorRef) { }

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
        .subscribe(data =>  {
          this.resultsLength = data.length;
          this.matTableDataSource.data = data;
          this.matTableDataSource.sort = this.matSort;
        });
  }

}
