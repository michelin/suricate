/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

import { DataTypeEnum } from '../../enums/data-type.enum';
import { IconEnum } from '../../enums/icon.enum';
import { PageModel } from '../../models/backend/page-model';
import { FormField } from '../../models/frontend/form/form-field';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { ListConfiguration } from '../../models/frontend/list/list-configuration';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { AbstractHttpService } from '../../services/backend/abstract-http/abstract-http.service';
import { HttpFilterService } from '../../services/backend/http-filter/http-filter.service';
import { DialogService } from '../../services/frontend/dialog/dialog.service';
import { FormService } from '../../services/frontend/form/form.service';
import { SidenavService } from '../../services/frontend/sidenav/sidenav.service';
import { ToastService } from '../../services/frontend/toast/toast.service';

/**
 * Generic component used to display and manage lists
 */
@Component({
  template: '',
  styleUrls: ['./list.component.scss'],
  standalone: true
})
export abstract class ListComponent<TRet, TReq> implements OnInit, OnDestroy {
  private readonly childService = inject<AbstractHttpService<TRet, TReq>>(AbstractHttpService);
  protected dialogService = inject(DialogService);
  protected sidenavService = inject(SidenavService);
  protected translateService = inject(TranslateService);
  protected toastService = inject(ToastService);
  protected router = inject(Router);
  private readonly formService = inject(FormService);

  /**
   * Key for the research field
   */
  public static readonly researchFormFieldKey = 'research';

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  protected unsubscribe: Subject<void> = new Subject<void>();

  /**
   * Subject used to emit the value when the research is modified
   */
  private readonly researchChanged: Subject<string> = new Subject<string>();

  /**
   * Configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The configuration of the list component
   */
  public listConfiguration = new ListConfiguration<TRet>();

  /**
   * Is drag & drop disabled
   */
  public dragAndDropDisabled = true;

  /**
   * The object list to display
   */
  public objectsPaged: PageModel<TRet>;

  /**
   * Display the loader when it's true, end hide when it's false
   */
  public isLoading = true;

  /**
   * Used to filter the list
   */
  protected httpFilter = HttpFilterService.getDefaultFilter();

  /**
   * List of icons
   */
  public iconEnum = IconEnum;

  /**
   * List of material icon
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Search bar configuration
   */
  public searchBarConfig: FormField;

  /**
   * The form displayed by the sidenav
   */
  public formGroup: UntypedFormGroup;

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.initSearchBarConfig();
    this.refreshList();
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Init the search bar configuration
   */
  private initSearchBarConfig(): void {
    this.searchBarConfig = {
      key: ListComponent.researchFormFieldKey,
      label: this.translateService.instant('search.bar'),
      iconPrefix: IconEnum.SEARCH,
      type: DataTypeEnum.TEXT
    };

    this.formGroup = this.formService.generateFormGroupForFields([this.searchBarConfig]);
    this.subscribeToInputSearchElement();
  }

  /**
   * Refresh the list displayed
   */
  protected refreshList(): void {
    this.displayLoader();

    this.childService.getAll(this.httpFilter).subscribe((objectsPaged: PageModel<TRet>) => {
      this.objectsPaged = objectsPaged;
      this.hideLoader();
      this.onItemsLoaded();
    });
  }

  /**
   * Function used to hide the loader
   */
  private hideLoader(): void {
    this.isLoading = false;
  }

  /**
   * Display the loader during the research
   */
  private displayLoader(): void {
    this.isLoading = true;
  }

  /**
   * Called when we change page from paginator
   *
   * @param pageEvent The page event
   */
  public pageChanged(pageEvent: PageEvent): void {
    this.httpFilter.page = pageEvent.pageIndex;
    this.httpFilter.size = pageEvent.pageSize;
    this.refreshList();
  }

  /**
   * Used to get the first label
   * Implemented by child component
   *
   * @param ignoredObject The object of the list
   */
  protected getFirstLabel(ignoredObject: TRet): string {
    return '';
  }

  /**
   * Used to get the second label
   * Implemented by child component
   *
   * @param ignoredObject The object of the list
   */
  protected getSecondLabel(ignoredObject: TRet): string {
    return '';
  }

  /**
   * Used to get the third label
   * Implemented by child component
   *
   * @param ignoredObject The object of the list
   */
  protected getThirdLabel(ignoredObject: TRet): string {
    return '';
  }

  /**
   * Used to retrieve the url of the associated object
   * Implemented in child component
   *
   * @param ignoredObject The object of the list
   */
  public getObjectImageURL(ignoredObject: TRet): string {
    return null;
  }

  /**
   * Redirect to bean detail
   *
   * @param ignoredObject The object used for the redirection
   */
  public redirectToBean(ignoredObject: TRet): void {
    // Implemented in child component
  }

  /**
   * Perform actions after items have been loaded
   */
  protected onItemsLoaded(): void {
    // Implemented in child component
  }

  /**
   * Subscribe to input search event
   * Wait for a few time before triggering the refresh
   */
  private subscribeToInputSearchElement(): void {
    this.researchChanged
      .pipe(takeUntil(this.unsubscribe), debounceTime(500), distinctUntilChanged())
      .subscribe((searchValue: string) => {
        this.httpFilter.page = 0;
        this.httpFilter.search = searchValue;
        this.refreshList();
      });
  }

  /**
   * Emit a new event when a new value is typed in the search bar
   */
  public researchChangedEvent(event: ValueChangedEvent) {
    this.researchChanged.next(event.value as string);
  }

  /**
   * When dragging & dropping an item, update its position
   * @param dropEvent The drag & drop event
   */
  public onDropEvent(dropEvent: CdkDragDrop<string[]>) {
    if (dropEvent.previousIndex !== dropEvent.currentIndex) {
      moveItemInArray(this.objectsPaged.content, dropEvent.previousIndex, dropEvent.currentIndex);
      this.drop();
    }
  }

  /**
   * Action to perform on drop
   */
  public drop(): void {
    // Implemented in child component
  }
}
