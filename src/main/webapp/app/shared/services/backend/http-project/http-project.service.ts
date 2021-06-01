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

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Project } from '../../../models/backend/project/project';
import { ProjectWidget } from '../../../models/backend/project-widget/project-widget';
import { ProjectRequest } from '../../../models/backend/project/project-request';
import { ProjectWidgetPositionRequest } from '../../../models/backend/project-widget/project-widget-position-request';
import { ProjectWidgetRequest } from '../../../models/backend/project-widget/project-widget-request';
import { User } from '../../../models/backend/user/user';
import { WebsocketClient } from '../../../models/backend/websocket-client';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';

@Injectable({ providedIn: 'root' })
export class HttpProjectService implements AbstractHttpService<Project | ProjectRequest> {
  /**
   * Global endpoint for projects
   * @type {string}
   */
  private static readonly projectsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/projects`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get every dashboards and update the list
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  public getAll(filter?: HttpFilter): Observable<Page<Project>> {
    const url = `${HttpProjectService.projectsApiEndpoint}`;

    return this.httpClient.get<Page<Project>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a dashboard by id
   *
   * @param {string} projectToken The dashboard token
   * @returns {Observable<Project>} The dashboard as observable
   */
  public getById(projectToken: string): Observable<Project> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}`;

    return this.httpClient.get<Project>(url);
  }

  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param {ProjectRequest} projectRequest The project request
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
   * @param {string} projectToken
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
   * Get the list of widget instances for a project
   *
   * @param projectToken The project token
   */
  public getWidgetInstancesByProjectToken(projectToken: string): Observable<ProjectWidget[]> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/projectWidgets`;

    return this.httpClient.get<ProjectWidget[]>(url);
  }

  /**
   * Add a new widget to the project
   *
   * @param projectToken The project token
   * @param projectWidgetRequest The project widget to add
   */
  public addProjectWidgetToProject(projectToken: string, projectWidgetRequest: ProjectWidgetRequest): Observable<ProjectWidget> {
    const url = `${HttpProjectService.projectsApiEndpoint}/${projectToken}/projectWidgets`;

    return this.httpClient.post<ProjectWidget>(url, projectWidgetRequest);
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
   * Get every dashboards for the current user
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  public getAllForCurrentUser(): Observable<Project[]> {
    const url = `${HttpProjectService.projectsApiEndpoint}/currentUser`;

    return this.httpClient.get<Project[]>(url);
  }
}
