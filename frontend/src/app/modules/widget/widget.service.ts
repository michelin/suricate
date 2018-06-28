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
import {Observable} from 'rxjs/Observable';
import {Category} from '../../shared/model/dto/Category';
import {Widget} from '../../shared/model/dto/Widget';

/**
 * Manage the widget Http calls
 */
@Injectable()
export class WidgetService extends AbstractHttpService {

  /**
   * Base URL for widgets
   * @type {string}
   * @private
   */
  private static readonly WIDGETS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.WIDGETS_URL}`;


  /**
   * Constructor
   *
   * @param {HttpClient} _httpClient The http client service
   */
  constructor(private _httpClient: HttpClient) {
    super();
  }

  /**
   * Get the list of widgets
   *
   * @returns {Observable<Widget[]>} The list of widgets as observable
   */
  getAll(): Observable<Widget[]> {
    return this._httpClient.get<Widget[]>(`${WidgetService.WIDGETS_BASE_URL}`);
  }

  /**
   * Update a widget
   *
   * @param {Widget} widget The widget to update
   * @returns {Observable<Widget>} The widget updated
   */
  updateWidget(widget: Widget): Observable<Widget> {
    return this._httpClient.post<Widget>(`${WidgetService.WIDGETS_BASE_URL}/${widget.id}`, widget);
  }

  /**
   * Retrieve every categories
   *
   * @returns {Observable<Category[]>} The categories as observable
   */
  getCategories(): Observable<Category[]> {
    return this._httpClient.get<Category[]>(`${WidgetService.WIDGETS_BASE_URL}/categories`);
  }

  /**
   * Get every widget for a category
   *
   * @param {number} categoryId The category id
   * @returns {Observable<Widget[]>} The widgets as observable
   */
  getWidgetsByCategoryId(categoryId: number): Observable<Widget[]> {
    return this._httpClient.get<Widget[]>(`${WidgetService.WIDGETS_BASE_URL}/category/${categoryId}`);
  }
}
