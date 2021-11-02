/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

import { Component, Injector, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ListConfiguration } from '../../models/frontend/list/list-configuration';
import { AbstractHttpService } from '../../services/backend/abstract-http/abstract-http.service';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { ToastService } from '../../services/frontend/toast/toast.service';
import { DialogService } from '../../services/frontend/dialog/dialog.service';
import { SidenavService } from '../../services/frontend/sidenav/sidenav.service';
import { Page } from '../../models/backend/page';
import { HttpFilterService } from '../../services/backend/http-filter/http-filter.service';
import { PageEvent } from '@angular/material/paginator';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { IconEnum } from '../../enums/icon.enum';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { DataTypeEnum } from '../../enums/data-type.enum';
import { FormGroup } from '@angular/forms';
import { FormField } from '../../models/frontend/form/form-field';
import { FormService } from '../../services/frontend/form/form.service';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';

/**
 * Generic component used to display and manage lists
 */
@Component({
  template: '',
  styleUrls: ['./list.component.scss']
})
export class ListComponent<T> implements OnInit, OnDestroy {
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
  private researchChanged: Subject<string> = new Subject<string>();

  /**
   * Frontend service used to display dialogs
   */
  protected dialogService: DialogService;

  /**
   * The sidenav service used to display the sidenav
   */
  protected sidenavService: SidenavService;

  /**
   * ngx-translate service used to manage the translations
   */
  protected translateService: TranslateService;

  /**
   * Frontend service used to display messages
   */
  protected toastService: ToastService;

  /**
   * The form service
   */
  private readonly formService: FormService;

  /**
   * Angular service used to manage routes
   */
  protected router: Router;

  /**
   * Configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The configuration of the list component
   */
  public listConfiguration = new ListConfiguration<T>();

  /**
   * The object list to display
   */
  public objectsPaged: Page<T>;

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
  public formGroup: FormGroup;

  /**
   * Constructor
   *
   * @param childService The child http service
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private readonly childService: AbstractHttpService<T>, protected injector: Injector) {
    this.dialogService = injector.get(DialogService);
    this.sidenavService = injector.get(SidenavService);
    this.translateService = injector.get(TranslateService);
    this.toastService = injector.get(ToastService);
    this.router = injector.get(Router);
    this.formService = injector.get(FormService);
  }

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

    this.childService.getAll(this.httpFilter).subscribe((objectsPaged: Page<T>) => {
      this.objectsPaged = objectsPaged;
      this.hideLoader();
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
   * Get the size of the block
   */
  public getDetailBlockSize(object: T): string {
    const hasImage = !!this.getObjectImageURL(object);
    const hasButtons = !!this.listConfiguration.buttons;

    if (hasImage && hasButtons) {
      return '60%';
    }
    if (hasImage || hasButtons) {
      return '80%';
    }
    return '100%';
  }

  /**
   * Called when we change page from paginator
   *
   * @param pageEvent The angular material page event
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
   * @param object The object of the list
   */
  protected getFirstLabel(object: T): string {
    return '';
  }

  /**
   * Used to get the second label
   * Implemented by child component
   *
   * @param object The object of the list
   */
  protected getSecondLabel(object: T): string {
    return '';
  }

  /**
   * Used to get the third label
   * Implemented by child component
   *
   * @param object The object of the list
   */
  protected getThirdLabel(object: T): string {
    return '';
  }

  /**
   * Used to retrieve the url of the associated object
   * Implemented in child component
   *
   * @param object The object of the list
   */
  public getObjectImageURL(object: T): string {
    return null;
  }

  /**
   * Redirect to bean detail
   * Implemented in child component
   *
   * @param object The object used for the redirection
   */
  public redirectToBean(object: T): void {}

  /**
   * Subscribe to input search event
   * Wait for a few time before triggering the refresh
   */
  private subscribeToInputSearchElement(): void {
    this.researchChanged.pipe(takeUntil(this.unsubscribe), debounceTime(500), distinctUntilChanged()).subscribe((searchValue: string) => {
      this.httpFilter.page = 0;
      this.httpFilter.search = searchValue;
      this.refreshList();
    });
  }

  /**
   * Emit a new event when a new value is typed in the search bar
   */
  public researchChangedEvent(event: ValueChangedEvent) {
    this.researchChanged.next(event.value);
  }
}
