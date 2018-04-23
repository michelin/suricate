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
import {Widget} from '../../../shared/model/dto/Widget';
import {WidgetService} from '../widget.service';
import {catchError} from 'rxjs/operators';
import {map} from 'rxjs/operators/map';
import {switchMap} from 'rxjs/operators/switchMap';
import {merge} from 'rxjs/observable/merge';
import {startWith} from 'rxjs/operators/startWith';
import {of as observableOf} from 'rxjs/observable/of';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {Asset} from '../../../shared/model/dto/Asset';

/**
 * Component that display the list of widgets (admin part)
 */
@Component({
  selector: 'app-widget-list',
  templateUrl: './widget-list.component.html',
  styleUrls: ['./widget-list.component.css']
})
export class WidgetListComponent implements OnInit {

  @ViewChild(MatSort) matSort: MatSort;
  @ViewChild(MatPaginator) matPaginator: MatPaginator;
  matTableDataSource = new MatTableDataSource();

  displayedColumns = ['image', 'name', 'description', 'category'];
  isLoadingResults = false;
  errorCatched = false;
  resultsLength = 0;

  /**
   * The list of widgets
   */
  widgets: Widget[];

  /**
   * Constructor
   *
   * @param {WidgetService} widgetService The widgetService to inject
   * @param {ChangeDetectorRef} changeDetectorRef enable the change detection after view init
   * @param {DomSanitizer} domSanitizer The dom sanitizer service
   */
  constructor(private widgetService: WidgetService,
              private changeDetectorRef: ChangeDetectorRef,
              private domSanitizer: DomSanitizer) {
  }

  /**
   * Steps when the component is init
   */
  ngOnInit() {
    this.initTable();
  }

  /**
   * Init the HTML table
   */
  initTable() {
    merge(this.matSort.sortChange, this.matPaginator.page)
        .pipe(
            startWith(null),
            switchMap(() => {
              this.isLoadingResults = true;
              this.changeDetectorRef.detectChanges();
              return this.widgetService.getAll();
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
        });

  }

  getHtmlImage(imageAsset: Asset): SafeHtml {
    let imgHtml: string;

    if (!imageAsset) {
      imgHtml = null;
    } else {
      imgHtml = `<img src="data:${imageAsset.contentType};base64,${imageAsset.content}" style="max-width: 100%; height: 105px" />`;
    }

    return this
        .domSanitizer
        .bypassSecurityTrustHtml(imgHtml);
  }

}
