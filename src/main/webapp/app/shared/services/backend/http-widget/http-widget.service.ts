/*
 * Copyright 2012-2021 the original author or authors.
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
import { HttpClient } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs';

import { Widget } from '../../../models/backend/widget/widget';
import { WidgetRequest } from '../../../models/backend/widget/widget-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';

/**
 * Manage the Http widget calls
 */
@Injectable({ providedIn: 'root' })
export class HttpWidgetService extends AbstractHttpService<Widget, WidgetRequest> {
  /**
   * Global endpoint for Widgets
   */
  private static readonly widgetsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/widgets`;

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private readonly httpClient: HttpClient) {
    super();
  }

  /**
   * Get the list of widgets
   *
   * @param {HttpFilter} filter Used to filter the result
   * @returns {Observable<Widget[]>} The list of widgets as observable
   */
  public getAll(filter?: HttpFilter): Observable<Page<Widget>> {
    const url = `${HttpWidgetService.widgetsApiEndpoint}`;
    return this.httpClient.get<Page<Widget>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a widget by the id
   *
   * @param widgetId
   */
  public getById(widgetId: number): Observable<Widget> {
    const url = `${HttpWidgetService.widgetsApiEndpoint}/${widgetId}`;

    return this.httpClient.get<Widget>(url);
  }

  /**
   * Create a widget
   *
   * @param widget The object that we want to create
   */
  public create(widget: WidgetRequest): Observable<Widget> {
    return EMPTY;
  }

  /**
   * Update the widget id
   *
   * @param widgetId The widget id
   * @param widgetRequest The widget request
   */
  public update(widgetId: number, widgetRequest: WidgetRequest): Observable<void> {
    const url = `${HttpWidgetService.widgetsApiEndpoint}/${widgetId}`;

    return this.httpClient.put<void>(url, widgetRequest);
  }

  /**
   * Function used to delete a widget
   *
   * @param widgetId The widget id
   */
  public delete(widgetId: number): Observable<void> {
    return EMPTY;
  }
}
