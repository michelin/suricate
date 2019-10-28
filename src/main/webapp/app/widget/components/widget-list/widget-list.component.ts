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

import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { fromEvent, merge, of as observableOf } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, startWith, switchMap } from 'rxjs/operators';

import { Widget } from '../../../shared/models/backend/widget/widget';
import { ToastService } from '../../../shared/services/frontend/toast.service';
import { UserService } from '../../../admin/services/user.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { HttpWidgetService } from '../../../shared/services/backend/http-widget.service';
import { WidgetAvailabilityEnum } from '../../../shared/enums/widget-availability.enum';
import { HttpAssetService } from '../../../shared/services/backend/http-asset.service';

/**
 * Component that display the list of widgets (admin part)
 */
@Component({
  selector: 'suricate-widget-list',
  templateUrl: './widget-list.component.html',
  styleUrls: ['./widget-list.component.scss']
})
export class WidgetListComponent implements OnInit, AfterViewInit, OnDestroy {
  /**
   * Manage the sort of each column on the table
   * @type {MatSort}
   */
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  /**
   * Manage the pagination
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator, { static: true }) matPaginator: MatPaginator;

  /**
   * The input filter for name
   */
  @ViewChild('nameInputFilter', { static: false }) nameInputFilter: ElementRef;
  /**
   * The input filter for category
   */
  @ViewChild('categoryInputFilter', { static: false }) categoryInputFilter: ElementRef;

  /**
   * True if the user is admin
   * @type {boolean}
   */
  isUserAdmin: boolean;

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
   * @param {UserService} userService The user service
   * @param {HttpWidgetService} httpWidgetService The http widget service
   * @param {HttpAssetService} httpAssetService The http asset service
   * @param {ChangeDetectorRef} changeDetectorRef enable the change detection after view init
   * @param {ToastService} toastService The toast notification service
   */
  constructor(
    private userService: UserService,
    private httpWidgetService: HttpWidgetService,
    private httpAssetService: HttpAssetService,
    private changeDetectorRef: ChangeDetectorRef,
    private toastService: ToastService
  ) {}

  /**
   * Steps when the component is init
   */
  ngOnInit() {
    this.isUserAdmin = this.userService.isAdmin();
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
          return this.httpWidgetService.getAll();
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
          return '';
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
    if (this.nameInputFilter !== undefined) {
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
    }

    if (this.categoryInputFilter !== undefined) {
      // Filter for widget category
      fromEvent(this.categoryInputFilter.nativeElement, 'keyup')
        .pipe(
          debounceTime(500),
          distinctUntilChanged(),
          map((keyboardEvent: any) => keyboardEvent.target.value)
        )
        .subscribe((inputValue: string) => {
          this.matTableDataSource.filterPredicate = (widget: Widget, filter: string) => {
            // return widget.category.name.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1;
            return true;
          };
          this.applyFilter(inputValue);
        });
    }
  }

  /**
   * Apply the column filter
   * @param {string} filterValue The value to search
   */
  applyFilter(filterValue: string) {
    filterValue = filterValue.trim().toLocaleLowerCase();
    this.matTableDataSource.filter = filterValue;
  }

  getImageSrc(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
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

      this.httpWidgetService.updateOneById(widgetToDisable.id, { widgetAvailability: widgetToDisable.widgetAvailability }).subscribe(() => {
        this.toastService.sendMessage(
          `The widget "${widgetToDisable.name}" has been ${widgetToDisable.widgetAvailability.toString()}`,
          ToastTypeEnum.SUCCESS
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
