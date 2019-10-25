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
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {merge, of as observableOf} from 'rxjs';
import {catchError, map, startWith, switchMap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';

import {HttpConfigurationService} from '../../../../../../shared/services/api/http-configuration.service';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';
import {Configuration} from '../../../../../../shared/model/api/configuration/Configuration';
import {ConfirmDialogComponent} from '../../../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {ToastType} from '../../../../../../shared/components/toast/toast-objects/ToastType';

/**
 * The configuration list component
 */
@Component({
  selector: 'app-widget-configuration-list',
  templateUrl: './widget-configuration-list.component.html',
  styleUrls: ['./widget-configuration-list.component.scss']
})
export class WidgetConfigurationListComponent implements OnInit {

  /**
   * manage sort of the Mat table
   * @type {MatSort}
   */
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  /**
   * manage pagination of the Mat table
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator, { static: true }) matPaginator: MatPaginator;

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
   * @param {HttpConfigurationService} configurationsService The configuration service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   * @param {TranslateService} translateService The translateService
   * @param {MatDialog} matDialog The mat dialog service
   * @param {ToastService} toastService The toast service
   */
  constructor(private configurationsService: HttpConfigurationService,
              private translateService: TranslateService,
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
    let deleteConfigurationDialog = null;

    this.translateService.get(['configuration.delete', 'delete.confirm']).subscribe(translations => {
      deleteConfigurationDialog = this.matDialog.open(ConfirmDialogComponent, {
        data: {
          title: translations['configuration.delete'],
          message: `${translations['delete.confirm']} ${configuration.key}`
        }
      });

      deleteConfigurationDialog.afterClosed().subscribe(shouldDeleteConfiguration => {
        if (shouldDeleteConfiguration) {
          this.configurationsService.deleteConfiguration(configuration.key).subscribe(() => {
            this.toastService.sendMessage('Configuration deleted successfully', ToastType.SUCCESS);
            this.initTable();
          });
        }
      });
    });
  }

}
