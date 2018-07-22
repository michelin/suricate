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

import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSlideToggleChange, MatSort, MatTableDataSource} from '@angular/material';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {merge, of as observableOf, fromEvent} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, takeWhile, map, switchMap, startWith} from 'rxjs/operators';


import {WidgetService} from '../../widget.service';
import {Asset} from '../../../../shared/model/dto/Asset';
import {WidgetAvailabilityEnum} from '../../../../shared/model/dto/enums/WidgetAvailabilityEnum';
import {Widget} from '../../../../shared/model/dto/Widget';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../shared/model/toastNotification/ToastType';

/**
 * Component that display the list of widgets (admin part)
 */
@Component({
  selector: 'app-widget-list',
  templateUrl: './widget-list.component.html',
  styleUrls: ['./widget-list.component.css']
})
export class WidgetListComponent implements OnInit, AfterViewInit, OnDestroy {

  /**
   * Manage the sort of each column on the table
   * @type {MatSort}
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * Manage the pagination
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * The input filter for name
   */
  @ViewChild('nameInputFilter') nameInputFilter: ElementRef;
  /**
   * The input filter for category
   */
  @ViewChild('categoryInputFilter') categoryInputFilter: ElementRef;

  /**
   * The table data source
   * @type {MatTableDataSource<any>}
   */
  matTableDataSource = new MatTableDataSource();
  /**
   * The column displayed
   * @type {string[]}
   */
  displayedColumns = ['image', 'name', 'description', 'category', 'status'];
  /**
   * Manage hide and show spinner when loading the table
   * @type {boolean}
   */
  isLoadingResults = false;
  /**
   * Check if we have an error while fetching lines
   * @type {boolean}
   */
  errorCatched = false;
  /**
   * The number of widget objects
   * @type {number}
   */
  resultsLength = 0;

  /**
   * The widget availability enums
   * @type {WidgetAvailabilityEnum}
   */
  widgetAvailability = WidgetAvailabilityEnum;

  /**
   * The list of widgets
   * @type {Widget[]}
   */
  widgets: Widget[];

  /**
   * True while the component is alive
   * @type {boolean}
   */
  private isAlive = true;

  /**
   * Constructor
   *
   * @param {WidgetService} widgetService The widgetService to inject
   * @param {ChangeDetectorRef} changeDetectorRef enable the change detection after view init
   * @param {DomSanitizer} domSanitizer The dom sanitizer service
   * @param {ToastService} toastService The toast notificaiton service
   */
  constructor(private widgetService: WidgetService,
              private changeDetectorRef: ChangeDetectorRef,
              private domSanitizer: DomSanitizer,
              private toastService: ToastService) {
  }

  /**
   * Steps when the component is init
   */
  ngOnInit() {
    this.widgetService.widgets$.pipe(
        takeWhile(() => this.isAlive)
    ).subscribe(() => {
      this.initTable();
    });

    this.initTable();
  }

  /**
   * When the view has been init
   */
  ngAfterViewInit() {
    this.initFilterSubscription();
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
        .subscribe(data => {
          this.resultsLength = data.length;
          this.matTableDataSource.data = data;
          this.matTableDataSource.sort = this.matSort;
          this.widgets = data;
        });

    // Apply custom sort rules
    this.matTableDataSource.sortingDataAccessor = (widget: Widget, property: string) => {
      switch (property) {
        case 'category':
          return widget.category.name;
        case 'status':
          return widget.widgetAvailability;
        default:
          return widget[property];
      }
    };
  }

  /**
   * Init the filter subscription
   */
  initFilterSubscription() {
    // Filter for widget name input
    fromEvent(this.nameInputFilter.nativeElement, 'keyup')
        .pipe(
            debounceTime(500),
            distinctUntilChanged(),
            map((keyboardEvent: any) => keyboardEvent.target.value)
        )
        .subscribe((inputValue: string) => {
          this.matTableDataSource.filterPredicate = (widget: Widget, filter: string) => {
            return widget.name.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1;
          };
          this.applyFilter(inputValue);
        });

    // Filter for widget category
    fromEvent(this.categoryInputFilter.nativeElement, 'keyup')
        .pipe(
            debounceTime(500),
            distinctUntilChanged(),
            map((keyboardEvent: any) => keyboardEvent.target.value)
        )
        .subscribe((inputValue: string) => {
          this.matTableDataSource.filterPredicate = (widget: Widget, filter: string) => {
            return widget.category.name.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1;
          };
          this.applyFilter(inputValue);
        });
  }

  /**
   * Apply the column filter
   * @param {string} filterValue The value to search
   */
  applyFilter(filterValue: string) {
    filterValue = filterValue.trim();
    this.matTableDataSource.filter = filterValue;
  }

  /**
   * Get the widget image as SafeHtml
   *
   * @param {Asset} imageAsset The asset to display
   * @returns {SafeHtml} The src html image as SafeHtml
   */
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

  /**
   * Enable or disable a widget
   *
   * @param {Widget} widget The widget to disable
   * @param {MatSlideToggleChange} changeEvent when click on the toggle slider
   */
  toggleWidgetActivation(widget: Widget, changeEvent: MatSlideToggleChange) {
    const widgetToDisable: Widget = this.widgets.find((currentWidget: Widget) => currentWidget.id === widget.id);

    if (widgetToDisable) {
      widgetToDisable.widgetAvailability = changeEvent.checked ? WidgetAvailabilityEnum.ACTIVATED : WidgetAvailabilityEnum.DISABLED;
      this
          .widgetService
          .updateWidget(widgetToDisable)
          .subscribe((widgetResponse: Widget) => {
            this.toastService.sendMessage(
                `The widget "${widgetResponse.name}" has been ${widgetResponse.widgetAvailability.toString()}`,
                ToastType.SUCCESS
            );
          });
    }
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }

}
