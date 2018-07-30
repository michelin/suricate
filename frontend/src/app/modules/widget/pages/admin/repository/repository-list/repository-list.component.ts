/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {merge, of} from 'rxjs/index';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';

import {RepositoryService} from '../repository.service';

@Component({
  selector: 'app-repository-list',
  templateUrl: './repository-list.component.html',
  styleUrls: ['./repository-list.component.css']
})
export class RepositoryListComponent implements OnInit {

  /**
   * manage sort of the Mat table
   * @type {MatSort}
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * manage pagination of the Mat table
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * Hold the data of the mat table
   * @type {MatTableDataSource<any>}
   */
  matTableDataSource = new MatTableDataSource();
  /**
   * The column references of the mat table
   * @type {string[]}
   */
  displayedColumns = ['name', 'localPath', 'url', 'branch', 'type', 'status'];
  /**
   * Is the results are loading
   * @type {boolean}
   */
  isLoadingResults = false;
  /**
   * If we have an error
   * @type {boolean}
   */
  errorCatched = false;
  /**
   * The number of rows
   * @type {number}
   */
  resultsLength = 0;

  /**
   * Constructor
   *
   * @param {MatDialog} matDialog The mat dialog service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector ref to inject
   * @param {RepositoryService} repositoryService The repository service
   */
  constructor(private matDialog: MatDialog,
              private changeDetectorRef: ChangeDetectorRef,
              private repositoryService: RepositoryService) {
  }

  /**
   * Called when the component is created
   */
  ngOnInit() {
    this.initTable();
  }

  /**
   * Init the list of repo
   */
  initTable() {
    merge(this.matSort.sortChange, this.matPaginator.page)
        .pipe(
            startWith(null),
            switchMap(() => {
              this.isLoadingResults = true;
              this.changeDetectorRef.detectChanges();
              return this.repositoryService.getAll();
            }),
            map(data => {
              this.isLoadingResults = false;
              this.errorCatched = false;

              return data;
            }),
            catchError(() => {
              this.isLoadingResults = false;
              this.errorCatched = true;

              return of([]);
            })
        )
        .subscribe(data => {
          this.resultsLength = data.length;
          this.matTableDataSource.data = data;
          this.matTableDataSource.sort = this.matSort;
        });
  }
}
