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

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { Page } from '../../../models/backend/page';
import { Project } from '../../../models/backend/project/project';
import { ProjectRequest } from '../../../models/backend/project/project-request';
import { ProjectWidgetPositionRequest } from '../../../models/backend/project-widget/project-widget-position-request';
import { User } from '../../../models/backend/user/user';
import { WebsocketClient } from '../../../models/backend/websocket-client';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilterService } from '../http-filter/http-filter.service';

@Injectable({ providedIn: 'root' })
export class HttpProjectService implements AbstractHttpService<Project, ProjectRequest> {
  /**
   * Global endpoint for projects
   */
  private static readonly projectsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/projects`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get all the projects
   *
   * @param filter The research/pagination filter
   */
  public getAll(filter?: HttpFilter): Observable<Page<Project>> {
    const url = `${HttpProjectService.projectsApiEndpoint}`;

    return this.httpClient.get<Page<Project>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a project by token
   *
   * @param projectToken The project token
   */
  public getById(projectToken: string): Observable<Project> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}`;

    return this.httpClient.get<Project>(url);
  }

  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param projectRequest The project request
   */
  public create(projectRequest: ProjectRequest): Observable<Project> {
    const url = `${HttpProjectService.projectsApiEndpoint}`;

    return this.httpClient.post<Project>(url, projectRequest);
  }

  /**
   * Update project
   *
   * @param projectToken The project token
   * @param projectRequest The project request
   */
  public update(projectToken: string, projectRequest: ProjectRequest): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}`;

    return this.httpClient.put<void>(url, projectRequest);
  }

  /**
   * Delete a project
   *
   * @param projectToken The project token
   */
  public delete(projectToken: string): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Add or update a screenshot for a project
   *
   * @param projectToken The project token
   * @param screenshotFile The screenshot file
   */
  public addOrUpdateProjectScreenshot(projectToken: string, screenshotFile: File): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/screenshot`;

    const formData: FormData = new FormData();
    formData.append('screenshot', screenshotFile, screenshotFile.name);

    const httpHeaders: HttpHeaders = new HttpHeaders();
    httpHeaders.append('Content-Type', 'multipart/form-data');
    httpHeaders.append('Accept', 'application/json');

    return this.httpClient.put<void>(url, formData, { headers: httpHeaders });
  }

  /**
   * Update the list of project widgets position for a project
   *
   * @param projectToken The project token
   * @param projectWidgetPositionRequests The list of positions to update
   */
  public updateProjectWidgetPositions(
    projectToken: string,
    projectWidgetPositionRequests: ProjectWidgetPositionRequest[]
  ): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/projectWidgetPositions`;

    return this.httpClient.put<void>(url, projectWidgetPositionRequests);
  }

  /**
   * Get the list of users for a project
   *
   * @param projectToken The project token
   */
  public getProjectUsers(projectToken: string): Observable<User[]> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/users`;

    return this.httpClient.get<User[]>(url);
  }

  /**
   * Add a user to a project
   *
   * @param {string} projectToken The projectToken
   * @param {string} username The username to add
   * @returns {Observable<Project>} The project as observable
   */
  public addUserToProject(projectToken: string, username: string): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/users`;

    return this.httpClient.post<void>(url, { username: username });
  }

  /**
   * Delete a user from a project
   *
   * @param {string} projectToken The project token
   * @param {number} userId The userId
   */
  public deleteUserFromProject(projectToken: string, userId: number): Observable<void> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/users/${userId}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the list of clients connected by websocket to a project
   *
   * @param projectToken The project token
   */
  public getProjectWebsocketClients(projectToken: string): Observable<WebsocketClient[]> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/websocket/clients`;
    return this.httpClient.get<WebsocketClient[]>(url);
  }

  /**
   * Get all dashboards of current user
   *
   * @returns The dashboards of the current user
   */
  public getAllForCurrentUser(): Observable<Project[]> {
    const url = `${HttpProjectService.projectsApiEndpoint}/currentUser`;

    return this.httpClient.get<Project[]>(url);
  }
}
