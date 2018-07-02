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
import {merge} from 'rxjs/observable/merge';
import {of as observableOf} from 'rxjs/observable/of';
import {catchError} from 'rxjs/operators';
import {map} from 'rxjs/operators/map';
import {startWith} from 'rxjs/operators/startWith';
import {switchMap} from 'rxjs/operators/switchMap';

import {DeleteConfigurationDialogComponent} from '../components/delete-configuration-dialog/delete-configuration-dialog.component';
import {ConfigurationService} from '../configuration.service';
import {ToastService} from '../../../shared/components/toast/toast.service';
import {Configuration} from '../../../shared/model/dto/Configuration';
import {ToastType} from '../../../shared/model/toastNotification/ToastType';

/**
 * The configuration list component
 */
@Component({
  selector: 'app-configuration-list',
  templateUrl: './configuration-list.component.html',
  styleUrls: ['./configuration-list.component.css']
})
export class ConfigurationListComponent implements OnInit {

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
  displayedColumns = ['key', 'value', 'type', 'category', 'edit', 'delete'];
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
   * The constructor
   *
   * @param {ConfigurationService} configurationsService The configuration service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   * @param {MatDialog} matDialog The mat dialog service
   * @param {ToastService} toastService The toast service
   */
  constructor(private configurationsService: ConfigurationService,
              private changeDetectorRef: ChangeDetectorRef,
              private matDialog: MatDialog,
              private toastService: ToastService) {
  }

  /**
   * When the component is created
   */
  ngOnInit() {
    this.initTable();
  }

  /**
   * Init the list table
   */
  initTable() {
    merge(this.matSort.sortChange, this.matPaginator.page)
        .pipe(
            startWith(null),
            switchMap(() => {
              this.isLoadingResults = true;
              this.changeDetectorRef.detectChanges();
              return this.configurationsService.getAll();
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

    // Apply sort custom rules for configuration
    this.matTableDataSource.sortingDataAccessor = (item: Configuration, property: string) => {
      switch (property) {
        case 'category':
          return item.category ? item.category.name : '';
        case 'type':
          return item.dataType ? item.dataType : '';
        default:
          return item[property];
      }
    };
  }

  /**
   * Open the delete configuration dialog
   * @param {Configuration} configuration The configuration to delete
   */
  openDialogDeleteConfiguration(configuration: Configuration) {
    const deleteConfigurationDialog = this.matDialog.open(DeleteConfigurationDialogComponent, {
      data: {configuration: configuration}
    });

    deleteConfigurationDialog.afterClosed().subscribe(shouldDeleteConfiguration => {
      if (shouldDeleteConfiguration) {
        this
            .configurationsService
            .deleteConfiguration(configuration)
            .subscribe(() => {
              this.toastService.sendMessage('Configuration deleted successfully', ToastType.SUCCESS);
              this.initTable();
            });
      }
    });
  }

}
