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

import { Category } from '../../../models/backend/category/category';
import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { Widget } from '../../../models/backend/widget/widget';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilterService } from '../http-filter/http-filter.service';

/**
 * Manage the widget Http calls
 */

@Injectable({ providedIn: 'root' })
export class HttpCategoryService {
	private static readonly categoriesApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/categories`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Retrieve the full list of categories
	 *
	 * @returns The categories as observable
	 */
	public getAll(filter?: HttpFilter): Observable<PageModel<Category>> {
		const url = `${HttpCategoryService.categoriesApiEndpoint}`;

		return this.httpClient.get<PageModel<Category>>(HttpFilterService.getFilteredUrl(url, filter));
	}

	/**
	 * Get the full list of widgets for a category
	 *
	 * @param categoryId The category id
	 */
	public getCategoryWidgets(categoryId: number): Observable<Widget[]> {
		const url = `${HttpCategoryService.categoriesApiEndpoint}/${categoryId}/widgets`;

		return this.httpClient.get<Widget[]>(url);
	}
}
