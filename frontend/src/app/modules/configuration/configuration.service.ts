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
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {Configuration} from '../../shared/model/dto/Configuration';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ConfigurationService extends AbstractHttpService {

  /**
   * Base URL for configurations
   *
   * @type {string}
   */
  private static readonly CONFIGURATIONS_BASE_URL = `${AbstractHttpService.BASE_URL}/${AbstractHttpService.CONFIGURATIONS_URL}`;

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {
    super();
  }

  getAll(): Observable<Configuration[]> {
    return this.httpClient.get<Configuration[]>(`${ConfigurationService.CONFIGURATIONS_BASE_URL}`);
  }
}
