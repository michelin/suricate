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

import { Component, OnInit } from '@angular/core';
import { ListConfiguration } from '../../models/frontend/list/list-configuration';
import { AbstractHttpService } from '../../services/backend/abstract-http.service';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';

/**
 * Generic component used to display and manage lists
 */
@Component({
  template: '',
  styleUrls: ['./list.component.scss']
})
export class ListComponent<T> implements OnInit {
  /**
   * Configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;
  /**
   * The configuration of the list component
   */
  public listConfiguration: ListConfiguration<T>;
  /**
   * The object list to display
   */
  public objects: T[];
  /**
   * Display the loader when it's true, end hide when it's false
   */
  public isLoading = true;

  /**
   * Constructor
   *
   * @param childService The child http service
   */
  constructor(private childService: AbstractHttpService<T>) {}

  /**
   * Called when the component is init
   */
  ngOnInit() {
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
}
