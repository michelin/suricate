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
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {startWith} from 'rxjs/operators/startWith';
import {of as observableOf} from 'rxjs/observable/of';
import {merge} from 'rxjs/observable/merge';
import {switchMap} from 'rxjs/operators/switchMap';
import {catchError} from 'rxjs/operators';
import {map} from 'rxjs/operators/map';
import {ConfigurationService} from '../configuration.service';

@Component({
  selector: 'app-configuration-list',
  templateUrl: './configuration-list.component.html',
  styleUrls: ['./configuration-list.component.css']
})
export class ConfigurationListComponent implements OnInit {

  @ViewChild(MatSort) matSort: MatSort;
  @ViewChild(MatPaginator) matPaginator: MatPaginator;
  matTableDataSource = new MatTableDataSource();

  displayedColumns = ['key', 'value', 'type', 'category'];
  isLoadingResults = false;
  errorCatched = false;
  resultsLength = 0;

  constructor(private configurationsService: ConfigurationService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.initTable();
  }

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

    this.matTableDataSource.sortingDataAccessor = (item: any, property) => {
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

}
