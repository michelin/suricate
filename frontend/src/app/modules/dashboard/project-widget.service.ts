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

import { Injectable } from '@angular/core';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {ProjectWidgetPosition} from '../../shared/model/dto/ProjectWidgetPosition';
import {Observable} from 'rxjs/Observable';
import {ProjectWidget} from '../../shared/model/dto/ProjectWidget';
import {HttpClient} from '@angular/common/http';

/**
 * Manage the project widget calls
 */
@Injectable()
export class ProjectWidgetService extends AbstractHttpService {

  /**
   * Base url
   */
  public static readonly PROJECT_WIDGETS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.PROJECT_WIDGETS_URL}`;

  /**
   * The constructor
   *
   * @param {HttpClient} httpClient The httpClient to inject
   */
  constructor(private httpClient: HttpClient) {
    super();
  }

  /**
   * Update the projectWidget position
   *
   * @param {number} projectWidgetId The project widget id to modify
   * @param {ProjectWidgetPosition} newPositionSize The project widget position holding the new position
   */
  updateWidgetPosition(projectWidgetId: number, newPositionSize: ProjectWidgetPosition): Observable<ProjectWidget> {
    return this.httpClient.put<ProjectWidget>(`${ProjectWidgetService.PROJECT_WIDGETS_BASE_URL}/${projectWidgetId}`, newPositionSize);
  }

}
