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

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectWidget} from '../../model/api/ProjectWidget/ProjectWidget';
import {projectWidgetsApiEndpoint} from '../../../app.constant';
import {ProjectWidgetRequest} from '../../model/api/ProjectWidget/ProjectWidgetRequest';

@Injectable()
export class HttpProjectWidgetService {

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Get a project widget by id
   *
   * @param projectWidgetId The project widget id
   */
  getOneById(projectWidgetId: number): Observable<ProjectWidget> {
    const url = `${projectWidgetsApiEndpoint}/${projectWidgetId}`;
    return this.httpClient.get<ProjectWidget>(url);
  }

  /**
   * Update a project widget by id
   *
   * @param projectWidgetId The project widget id
   * @param projectWidgetRequest The new project widget
   */
  updateOneById(projectWidgetId: number, projectWidgetRequest: ProjectWidgetRequest): Observable<void> {
    const url = `${projectWidgetsApiEndpoint}/${projectWidgetId}`;
    return this.httpClient.put<void>(url, projectWidgetRequest);
  }

  /**
   * Delete a project widget by id
   *
   * @param projectWidgetId The project widget id
   */
  deleteOneById(projectWidgetId: number): Observable<void> {
    const url = `${projectWidgetsApiEndpoint}/${projectWidgetId}`;
    return this.httpClient.delete<void>(url);
  }
}