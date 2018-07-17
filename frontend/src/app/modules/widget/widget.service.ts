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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {tap} from 'rxjs/operators';

import {Category} from '../../shared/model/dto/Category';
import {Widget} from '../../shared/model/dto/Widget';
import {widgetsApiEndpoint} from '../../app.constant';

/**
 * Manage the widget Http calls
 */
@Injectable()
export class WidgetService {

  /**
   * Subject that hold events when list has been reloaded
   *
   * @type {Subject<Widget[]>}
   */
  private widgetsSubject = new Subject<Widget[]>();

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {
  }

  /* ******************************************************************* */
  /*                      Subject Management Part                        */

  /* ******************************************************************* */

  /**
   * Subscribe to widgets event
   *
   * @return {Observable<Widget[]>}
   */
  get widgets$(): Observable<Widget[]> {
    return this.widgetsSubject.asObservable();
  }

  /**
   * Send an event with the new list of widgets
   *
   * @param {Widget[]} widgets
   */
  set widgets(widgets: Widget[]) {
    this.widgetsSubject.next(widgets);
  }

  /* ******************************************************************* */
  /*                        Widget HTTP Management                       */

  /* ******************************************************************* */

  /**
   * Get the list of widgets
   *
   * @param {boolean} refreshWidgets True if we should refresh the widgets false otherwise
   * @returns {Observable<Widget[]>} The list of widgets as observable
   */
  getAll(refreshWidgets = false): Observable<Widget[]> {
    const url = `${widgetsApiEndpoint}?refresh-widgets=${refreshWidgets}`;

    return this.httpClient.get<Widget[]>(url).pipe(
        tap(widgets => {
          if (refreshWidgets) {
            this.widgets = widgets;
          }
        })
    );
  }

  /**
   * Update a widget
   *
   * @param {Widget} widget The widget to update
   * @returns {Observable<Widget>} The widget updated
   */
  updateWidget(widget: Widget): Observable<Widget> {
    const url = `${widgetsApiEndpoint}/${widget.id}`;

    return this.httpClient.post<Widget>(url, widget);
  }

  /**
   * Retrieve every categories
   *
   * @returns {Observable<Category[]>} The categories as observable
   */
  getCategories(): Observable<Category[]> {
    const url = `${widgetsApiEndpoint}/categories`;

    return this.httpClient.get<Category[]>(url);
  }

  /**
   * Get every widget for a category
   *
   * @param {number} categoryId The category id
   * @returns {Observable<Widget[]>} The widgets as observable
   */
  getWidgetsByCategoryId(categoryId: number): Observable<Widget[]> {
    const url = `${widgetsApiEndpoint}/category/${categoryId}`;

    return this.httpClient.get<Widget[]>(url);
  }
}
