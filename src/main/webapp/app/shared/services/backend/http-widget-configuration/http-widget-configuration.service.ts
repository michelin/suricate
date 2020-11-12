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
import { EMPTY, Observable } from 'rxjs';

import { WidgetConfiguration } from '../../../models/backend/widget-configuration/widget-configuration';
import { ApplicationProperties } from '../../../models/backend/application-properties';
import { WidgetConfigurationRequest } from '../../../models/backend/widget-configuration/widget-configuration-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpWidgetConfigurationService extends AbstractHttpService<WidgetConfiguration> {
  /**
   * Global configurations enpoint
   * @type {string}
   */
  private static readonly configurationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/configurations`;

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private readonly httpClient: HttpClient) {
    super();
  }

  /**
   * Get the full list of configuration
   *
   * @returns {Observable<WidgetConfiguration[]>} The configuration as observable
   */
  public getAll(filter?: HttpFilter): Observable<Page<WidgetConfiguration>> {
    const url = `${HttpWidgetConfigurationService.configurationsApiEndpoint}`;

    return this.httpClient.get<Page<WidgetConfiguration>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a single configuration by key
   *
   * @param {string} configurationKey The key to find
   * @returns {Observable<WidgetConfiguration>} The configuration as observable
   */
  public getById(configurationKey: string): Observable<WidgetConfiguration> {
    const url = `${HttpWidgetConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.get<WidgetConfiguration>(url);
  }

  /**
   * Function used to create a configuration
   *
   * @param configuration The configuration that we want to create
   */
  public create(configuration: WidgetConfiguration): Observable<WidgetConfiguration> {
    return EMPTY;
  }

  /**
   * Update a configuration
   *
   * @param {string} configurationKey The configuration key to update
   * @param {WidgetConfigurationRequest} configurationRequest The value updated
   * @returns {Observable<WidgetConfiguration>} The config updated
   */
  public update(configurationKey: string, configurationRequest: WidgetConfigurationRequest): Observable<void> {
    const url = `${HttpWidgetConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.put<void>(url, configurationRequest);
  }

  /**
   * Delete the configuration
   *
   * @param {string} configurationKey The configuration to delete
   * @returns {Observable<WidgetConfiguration>} The configuration delete as observable
   */
  public delete(configurationKey: string): Observable<void> {
    const url = `${HttpWidgetConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the server configuration for authentication provider
   *
   * @return {Observable<ApplicationProperties>} Configuration for Authentication Provider
   */
  public getAuthenticationProvider(): Observable<ApplicationProperties> {
    const url = `${HttpWidgetConfigurationService.configurationsApiEndpoint}/authentication-provider`;

    return this.httpClient.get<ApplicationProperties>(url);
  }
}
