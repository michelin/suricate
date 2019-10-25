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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { categoriesApiEndpoint } from '../../../app.constant';
import { Category } from '../../model/api/widget/Category';
import { Widget } from '../../model/api/widget/Widget';
import { Configuration } from '../../model/api/configuration/Configuration';

/**
 * Manage the widget Http calls
 */

@Injectable({ providedIn: 'root' })
export class HttpCategoryService {
  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private httpClient: HttpClient) {}

  /**
   * Retrieve the full list of categories
   *
   * @returns {Observable<Category[]>} The categories as observable
   */
  getAll(): Observable<Category[]> {
    const url = `${categoriesApiEndpoint}`;

    return this.httpClient.get<Category[]>(url);
  }

  /**
   * Get the list of configurations for a category
   * @param categoryId The category id
   */
  getCategoryConfigurations(categoryId: number): Observable<Configuration[]> {
    const url = `${categoriesApiEndpoint}/${categoryId}/configurations`;
    return this.httpClient.get<Configuration[]>(url);
  }

  /**
   * Get the full list of widgets for a category
   *
   * @param categoryId The category id
   */
  getCategoryWidgets(categoryId: number): Observable<Widget[]> {
    const url = `${categoriesApiEndpoint}/${categoryId}/widgets`;

    return this.httpClient.get<Widget[]>(url);
  }
}
