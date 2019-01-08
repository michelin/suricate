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

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {Widget} from '../../model/api/widget/Widget';
import {widgetsApiEndpoint} from '../../../app.constant';
import {ApiActionEnum} from '../../model/enums/ApiActionEnum';
import {WidgetRequest} from '../../model/api/widget/WidgetRequest';

@Injectable()
export class HttpWidgetService {

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Get the list of widgets
   *
   * @param {ApiActionEnum} action Action to be executed by the backend
   * @returns {Observable<Widget[]>} The list of widgets as observable
   */
  getAll(action?: ApiActionEnum): Observable<Widget[]> {
    let url = `${widgetsApiEndpoint}`;
    if (action) {
      url = url.concat(`?action=${action}`);
    }

    return this.httpClient.get<Widget[]>(url);
  }

  /**
   * Get a widget by the id
   *
   * @param widgetId
   */
  getOneById(widgetId: number): Observable<Widget> {
    const url = `${widgetsApiEndpoint}/`;

    return this.httpClient.get<Widget>(url);
  }

  /**
   * Update the widget id
   *
   * @param widgetId The widget id
   * @param widgetRequest The widget request
   */
  updateOneById(widgetId: number, widgetRequest: WidgetRequest): Observable<void> {
    const url = `${widgetsApiEndpoint}/${widgetId}`;

    return this.httpClient.put<void>(url, widgetRequest);
  }
}