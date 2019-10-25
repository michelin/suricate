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

import { Configuration } from '../../model/api/configuration/Configuration';
import { configurationsApiEndpoint } from '../../../app.constant';
import { ApplicationProperties } from '../../model/api/ApplicationProperties';
import { ConfigurationRequest } from '../../model/api/configuration/ConfigurationRequest';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpConfigurationService {
  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {}

  /**
   * Get the full list of configuration
   *
   * @returns {Observable<Configuration[]>} The configuration as observable
   */
  getAll(): Observable<Configuration[]> {
    const url = `${configurationsApiEndpoint}`;

    return this.httpClient.get<Configuration[]>(url);
  }

  /**
   * Get a single configuration by key
   *
   * @param {string} configurationKey The key to find
   * @returns {Observable<Configuration>} The configuration as observable
   */
  getOneByKey(configurationKey: string): Observable<Configuration> {
    const url = `${configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.get<Configuration>(url);
  }

  /**
   * Update a configuration
   *
   * @param {string} configurationKey The configuration key to update
   * @param {ConfigurationRequest} configurationRequest The value updated
   * @returns {Observable<Configuration>} The config updated
   */
  updateConfigurationByKey(configurationKey: string, configurationRequest: ConfigurationRequest): Observable<void> {
    const url = `${configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.put<void>(url, configurationRequest);
  }

  /**
   * Delete the configuration
   *
   * @param {string} configurationKey The configuration to delete
   * @returns {Observable<Configuration>} The configuration delete as observable
   */
  deleteConfiguration(configurationKey: string): Observable<void> {
    const url = `${configurationsApiEndpoint}/${configurationKey}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the server configuration for authentication provider
   *
   * @return {Observable<ApplicationProperties>} Configuration for Authentication Provider
   */
  getAuthenticationProvider(): Observable<ApplicationProperties> {
    const url = `${configurationsApiEndpoint}/authentication-provider`;

    return this.httpClient.get<ApplicationProperties>(url);
  }
}
