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

import { Configuration } from '../../models/backend/configuration/configuration';
import { ApplicationProperties } from '../../models/backend/application-properties';
import { ConfigurationRequest } from '../../models/backend/configuration/configuration-request';
import { AbstractHttpService } from './abstract-http.service';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpConfigurationService extends AbstractHttpService<Configuration> {
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
   * @returns {Observable<Configuration[]>} The configuration as observable
   */
  public getAll(): Observable<Configuration[]> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}`;

    return this.httpClient.get<Configuration[]>(url);
  }

  /**
   * Get a single configuration by key
   *
   * @param {string} configurationKey The key to find
   * @returns {Observable<Configuration>} The configuration as observable
   */
  public getById(configurationKey: string): Observable<Configuration> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.get<Configuration>(url);
  }

  /**
   * Function used to create a configuration
   *
   * @param configuration The configuration that we want to create
   */
  public create(configuration: Configuration): Observable<Configuration> {
    return EMPTY;
  }

  /**
   * Update a configuration
   *
   * @param {string} configurationKey The configuration key to update
   * @param {ConfigurationRequest} configurationRequest The value updated
   * @returns {Observable<Configuration>} The config updated
   */
  public update(configurationKey: string, configurationRequest: ConfigurationRequest): Observable<void> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.put<void>(url, configurationRequest);
  }

  /**
   * Delete the configuration
   *
   * @param {string} configurationKey The configuration to delete
   * @returns {Observable<Configuration>} The configuration delete as observable
   */
  public delete(configurationKey: string): Observable<void> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the server configuration for authentication provider
   *
   * @return {Observable<ApplicationProperties>} Configuration for Authentication Provider
   */
  public getAuthenticationProvider(): Observable<ApplicationProperties> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}/authentication-provider`;

    return this.httpClient.get<ApplicationProperties>(url);
  }
}
