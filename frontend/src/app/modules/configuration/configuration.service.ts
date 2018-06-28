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

import {Injectable} from '@angular/core';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {Configuration} from '../../shared/model/dto/Configuration';
import {Observable} from 'rxjs/Observable';

/**
 * Configuration services manage http calls
 */
@Injectable()
export class ConfigurationService extends AbstractHttpService {

  /**
   * Base URL for configurations
   * @type {string}
   * @private
   */
  private static readonly _CONFIGURATIONS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.CONFIGURATIONS_URL}`;

  /**
   * Constructor
   *
   * @param {HttpClient} _httpClient The http client service
   */
  constructor(private _httpClient: HttpClient) {
    super();
  }

  /**
   * Get the full list of configuration
   *
   * @returns {Observable<Configuration[]>} The configuration as observable
   */
  getAll(): Observable<Configuration[]> {
    return this._httpClient.get<Configuration[]>(`${ConfigurationService._CONFIGURATIONS_BASE_URL}`);
  }

  /**
   * Get a single configuration by key
   *
   * @param {string} key The key to find
   * @returns {Observable<Configuration>} The configuration as observable
   */
  getOneByKey(key: string): Observable<Configuration> {
    return this._httpClient.get<Configuration>(`${ConfigurationService._CONFIGURATIONS_BASE_URL}/${key}`);
  }

  /**
   * Update a configuration
   *
   * @param {Configuration} configuration The ocnfiguration to update
   * @returns {Observable<Configuration>} The config updated
   */
  updateConfigurationByKey(configuration: Configuration): Observable<Configuration> {
    return this._httpClient.put<Configuration>(`${ConfigurationService._CONFIGURATIONS_BASE_URL}/${configuration.key}`, configuration);
  }

  /**
   * Delete the configuration
   *
   * @param {Configuration} configuration The configuration to delete
   * @returns {Observable<Configuration>} The configuration delete as observable
   */
  deleteConfiguration(configuration: Configuration): Observable<Configuration> {
    return this._httpClient.delete<Configuration>(`${ConfigurationService._CONFIGURATIONS_BASE_URL}/${configuration.key}`);
  }
}
