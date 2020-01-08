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

import { AfterViewInit, Component, ElementRef, Injector, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { ListConfiguration } from '../../models/frontend/list/list-configuration';
import { AbstractHttpService } from '../../services/backend/abstract-http.service';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { ToastService } from '../../services/frontend/toast.service';
import { DialogService } from '../../services/frontend/dialog.service';
import { SidenavService } from '../../services/frontend/sidenav.service';
import { Page } from '../../models/backend/page';
import { HttpFilterService } from '../../services/backend/http-filter.service';
import { PageEvent } from '@angular/material';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { IconEnum } from '../../enums/icon.enum';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, takeWhile } from 'rxjs/operators';

/**
 * Generic component used to display and manage lists
 */
@Component({
  template: '',
  styleUrls: ['./list.component.scss']
})
export class ListComponent<T> implements OnInit, AfterViewInit, OnDestroy {
  /**
   * Reference on input search
   */
  @ViewChild('inputSearch', { static: false })
  public inputSearch: ElementRef<HTMLInputElement>;
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
  protected listConfiguration = new ListConfiguration<unknown>();
  /**
   * The object list to display
   */
  protected objectsPaged: Page<T>;
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
   * Tell if the component is alive
   */
  public isAlive = true;

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
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.refreshList();
  }

  /**
   * Called when the view has been init
   */
  public ngAfterViewInit(): void {
    this.subscribeToInputSearchElement();
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
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

  private displayLoader(): void {
    this.isLoading = true;
  }

  /**
   * Get the size of the block
   */
  protected getDetailBlockSize(object: T): string {
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
  private pageChanged(pageEvent: PageEvent): void {
    console.log(pageEvent);
    this.httpFilter.page = pageEvent.pageIndex;
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
  protected getObjectImageURL(object: T): string {
    return null;
  }

  /**
   * Redirect to bean detail
   * Implemented in child component
   *
   * @param object The object used for the redirection
   */
  protected redirectToBean(object: T): void {}

  /**
   * Subscribe to input search event
   */
  private subscribeToInputSearchElement(): void {
    fromEvent(this.inputSearch.nativeElement, 'keyup')
      .pipe(
        takeWhile(() => this.isAlive),
        debounceTime(500),
        map((event: Event) => (event.target as HTMLInputElement).value),
        distinctUntilChanged()
      )
      .subscribe((searchValue: string) => {
        this.httpFilter.search = searchValue;
        this.refreshList();
      });
  }
}
