/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Manage the app theme
 */
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {configApiEndpoint} from '../../../app.constant';
import {Observable} from 'rxjs/index';

@Injectable()
export class ConfigService {

  /**
   * The constructor
   *
   * @param {HttpClient} httpClient The http client service to inject
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Get the list of configuration keys
   *
   * @returns {Observable<string>} The list of configuration keys
   */
  getAll(): Observable<Object> {
    const url = `${configApiEndpoint}/configprops`;

    return this.httpClient.get<Object>(url);
  }
}
