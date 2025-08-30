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

import { ProjectWidget } from '../../../models/backend/project-widget/project-widget';
import { ProjectWidgetRequest } from '../../../models/backend/project-widget/project-widget-request';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Manage the http project widget calls
 */
@Injectable({ providedIn: 'root' })
export class HttpProjectWidgetService {
	private static readonly projectWidgetsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/projectWidgets`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get a project widget by id
	 *
	 * @param projectWidgetId The project widget id
	 */
	public getOneById(projectWidgetId: number): Observable<ProjectWidget> {
		const url = `${HttpProjectWidgetService.projectWidgetsApiEndpoint}/${projectWidgetId}`;
		return this.httpClient.get<ProjectWidget>(url);
	}

	/**
	 * Get the list of widget instances for a project
	 *
	 * @param projectToken The project token
	 */
	public getAllByProjectToken(projectToken: string): Observable<ProjectWidget[]> {
		const url = `${HttpProjectWidgetService.projectWidgetsApiEndpoint}/${projectToken}/projectWidgets`;

		return this.httpClient.get<ProjectWidget[]>(url);
	}

	/**
	 * Add a new widget to the project
	 *
	 * @param projectToken The project token
	 * @param gridId The grid id
	 * @param projectWidgetRequest The project widget to add
	 */
	public addProjectWidgetToProject(
		projectToken: string,
		gridId: number,
		projectWidgetRequest: ProjectWidgetRequest
	): Observable<ProjectWidget> {
		const url = `${HttpProjectWidgetService.projectWidgetsApiEndpoint}/${projectToken}/${gridId}/projectWidgets`;

		return this.httpClient.post<ProjectWidget>(url, projectWidgetRequest);
	}

	/**
	 * Update a project widget by id
	 *
	 * @param projectWidgetId The project widget id
	 * @param projectWidgetRequest The new project widget
	 */
	public updateOneById(projectWidgetId: number, projectWidgetRequest: ProjectWidgetRequest): Observable<ProjectWidget> {
		const url = `${HttpProjectWidgetService.projectWidgetsApiEndpoint}/${projectWidgetId}`;
		return this.httpClient.put<ProjectWidget>(url, projectWidgetRequest);
	}

	/**
	 * Delete a project widget by id
	 *
	 * @param projectWidgetId The project widget id
	 */
	public deleteOneById(projectWidgetId: number): Observable<void> {
		const url = `${HttpProjectWidgetService.projectWidgetsApiEndpoint}/${projectWidgetId}`;
		return this.httpClient.delete<void>(url);
	}
}
