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

import { Component, Injector, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { ListConfiguration } from '../../models/frontend/list/list-configuration';
import { AbstractHttpService } from '../../services/backend/abstract-http.service';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { ToastService } from '../../services/frontend/toast.service';
import { DialogService } from '../../services/frontend/dialog.service';
import { SidenavService } from '../../services/frontend/sidenav.service';

/**
 * Generic component used to display and manage lists
 */
@Component({
  template: '',
  styleUrls: ['./list.component.scss']
})
export class ListComponent<T> implements OnInit {
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
  protected headerConfiguration: HeaderConfiguration;
  /**
   * The configuration of the list component
   */
  protected listConfiguration = new ListConfiguration<unknown>();
  /**
   * The object list to display
   */
  protected objects: T[];
  /**
   * Display the loader when it's true, end hide when it's false
   */
  protected isLoading = true;

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
   * Refresh the list displayed
   */
  protected refreshList(): void {
    this.displayLoader();
    this.childService.getAll().subscribe((objects: T[]) => {
      this.objects = objects;
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
}
