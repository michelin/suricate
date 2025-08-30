/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { Widget } from '../../../models/backend/widget/widget';
import { WidgetRequest } from '../../../models/backend/widget/widget-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilterService } from '../http-filter/http-filter.service';

/**
 * Manage the Http widget calls
 */
@Injectable({ providedIn: 'root' })
export class HttpWidgetService extends AbstractHttpService<Widget, WidgetRequest> {
	private static readonly widgetsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/widgets`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get the list of widgets
	 *
	 * @param {HttpFilter} filter Used to filter the result
	 * @returns {Observable<Widget[]>} The list of widgets as observable
	 */
	public getAll(filter?: HttpFilter): Observable<PageModel<Widget>> {
		const url = `${HttpWidgetService.widgetsApiEndpoint}`;
		return this.httpClient.get<PageModel<Widget>>(HttpFilterService.getFilteredUrl(url, filter));
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
	 */
	public create(): Observable<Widget> {
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
	 */
	public delete(id: number): Observable<void> {
		const url = `${HttpWidgetService.widgetsApiEndpoint}/${id}`;

		return this.httpClient.delete<void>(url);
	}
}
