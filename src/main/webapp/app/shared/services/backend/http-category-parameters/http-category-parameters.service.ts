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
import { Observable } from 'rxjs';

import { CategoryParameter } from '../../../models/backend/category-parameters/category-parameter';
import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { WidgetConfigurationRequest } from '../../../models/backend/widget-configuration/widget-configuration-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilterService } from '../http-filter/http-filter.service';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpCategoryParametersService extends AbstractHttpService<CategoryParameter, WidgetConfigurationRequest> {
	private static readonly configurationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/category-parameters`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get all parameters of all categories
	 *
	 * @param filter The filter
	 */
	public getAll(filter?: HttpFilter): Observable<PageModel<CategoryParameter>> {
		const url = `${HttpCategoryParametersService.configurationsApiEndpoint}`;

		return this.httpClient.get<PageModel<CategoryParameter>>(HttpFilterService.getFilteredUrl(url, filter));
	}

	/**
	 * Get a category parameter by key
	 *
	 * @param categoryParameterKey The category parameter key
	 */
	public getById(categoryParameterKey: string): Observable<CategoryParameter> {
		const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${categoryParameterKey}`;

		return this.httpClient.get<CategoryParameter>(url);
	}

	/**
	 * Function used to create a configuration
	 */
	public create(widgetConfigurationRequest: WidgetConfigurationRequest): Observable<CategoryParameter> {
		const url = `${HttpCategoryParametersService.configurationsApiEndpoint}`;

		return this.httpClient.post<CategoryParameter>(url, widgetConfigurationRequest);
	}

	/**
	 * Update a configuration
	 *
	 * @param configurationKey The configuration key to update
	 * @param configurationRequest The value updated
	 * @returns The config updated
	 */
	public update(configurationKey: string, configurationRequest: WidgetConfigurationRequest): Observable<void> {
		const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${configurationKey}`;

		return this.httpClient.put<void>(url, configurationRequest);
	}

	/**
	 * Delete the configuration
	 *
	 * @param configurationKey The configuration to delete
	 * @returns The configuration delete as observable
	 */
	public delete(configurationKey: string): Observable<void> {
		const url = `${HttpCategoryParametersService.configurationsApiEndpoint}/${configurationKey}`;

		return this.httpClient.delete<void>(url);
	}
}
