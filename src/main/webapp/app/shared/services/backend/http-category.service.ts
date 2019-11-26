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

import { Category } from '../../models/backend/widget/category';
import { Widget } from '../../models/backend/widget/widget';
import { Configuration } from '../../models/backend/configuration/configuration';
import { AbstractHttpService } from './abstract-http.service';

/**
 * Manage the widget Http calls
 */

@Injectable({ providedIn: 'root' })
export class HttpCategoryService {
  /**
   * Global endpoint for Widgets
   * @type {string}
   */
  private static readonly categoriesApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/categories`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Retrieve the full list of categories
   *
   * @returns {Observable<Category[]>} The categories as observable
   */
  public getAll(): Observable<Category[]> {
    const url = `${HttpCategoryService.categoriesApiEndpoint}`;

    return this.httpClient.get<Category[]>(url);
  }

  /**
   * Get the list of configurations for a category
   * @param categoryId The category id
   */
  public getCategoryConfigurations(categoryId: number): Observable<Configuration[]> {
    const url = `${HttpCategoryService.categoriesApiEndpoint}/${categoryId}/configurations`;
    return this.httpClient.get<Configuration[]>(url);
  }

  /**
   * Get the full list of widgets for a category
   *
   * @param categoryId The category id
   */
  public getCategoryWidgets(categoryId: number): Observable<Widget[]> {
    const url = `${HttpCategoryService.categoriesApiEndpoint}/${categoryId}/widgets`;

    return this.httpClient.get<Widget[]>(url);
  }
}
