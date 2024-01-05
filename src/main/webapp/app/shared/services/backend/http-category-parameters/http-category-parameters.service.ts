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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs';
import { WidgetConfigurationRequest } from '../../../models/backend/widget-configuration/widget-configuration-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';
import { CategoryParameter } from '../../../models/backend/category-parameters/category-parameter';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpCategoryParametersService extends AbstractHttpService<CategoryParameter, WidgetConfigurationRequest> {
  /**
   * Global configurations endpoint
   */
  private static readonly configurationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/category-parameters`;

  /**
   * Constructor
   *
   * @param httpClient The http client service
   */
  constructor(private readonly httpClient: HttpClient) {
    super();
  }

  /**
   * Get all parameters of all categories
   *
   * @param filter The filter
   */
  public getAll(filter?: HttpFilter): Observable<Page<CategoryParameter>> {
    const url = `${HttpCategoryParametersService.configurationsApiEndpoint}`;

    return this.httpClient.get<Page<CategoryParameter>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a category parameter by key
   *
   * @param categoryParameterKey The category parameter key
   */
  public getById(categoryParameterKey: string): Observable<CategoryParameter> {
    const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${categoryParameterKey}`;

    return this.httpClient.get<CategoryParameter>(url);
  }

  /**
   * Function used to create a configuration
   *
   * @param configuration The configuration that we want to create
   */
  public create(configuration: WidgetConfigurationRequest): Observable<CategoryParameter> {
    return EMPTY;
  }

  /**
   * Update a configuration
   *
   * @param configurationKey The configuration key to update
   * @param configurationRequest The value updated
   * @returns The config updated
   */
  public update(configurationKey: string, configurationRequest: WidgetConfigurationRequest): Observable<void> {
    const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.put<void>(url, configurationRequest);
  }

  /**
   * Delete the configuration
   *
   * @param configurationKey The configuration to delete
   * @returns The configuration delete as observable
   */
  public delete(configurationKey: string): Observable<void> {
    const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.delete<void>(url);
  }
}
