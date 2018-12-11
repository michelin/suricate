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
import {Observable} from 'rxjs';

import {Project} from '../../model/api/Project';
import {projectsApiEndpoint} from '../../../app.constant';
import {DashboardService} from '../../../modules/dashboard/dashboard.service';
import {map} from 'rxjs/operators';
import {ProjectWidget} from '../../model/api/ProjectWidget';
import {ProjectWidgetPosition} from '../../model/api/ProjectWidgetPosition';

@Injectable()
export class HttpProjectService {

  constructor(private httpClient: HttpClient,
              private dashboardService: DashboardService) {
  }

  /**
   * Get every dashboards and update the list
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  getAll(): Observable<Project[]> {
    const url = `${projectsApiEndpoint}`;

    return this.httpClient.get<Project[]>(url);
  }

  /**
   * Get every dashboards for the current user
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  getAllForCurrentUser(): Observable<Project[]> {
    const url = `${projectsApiEndpoint}/currentUser`;

    return this.httpClient.get<Project[]>(url)
      .pipe(
        map(projects => {
          this.dashboardService.currentDashboardListValues = projects;
          return projects;
        })
      );
  }

  /**
   * Get a dashboard by id
   *
   * @param {number} id The dashboard id
   * @returns {Observable<Project>} The dashboard as observable
   */
  getOneById(id: number): Observable<Project> {
    const url = `${projectsApiEndpoint}/${id}`;

    return this.httpClient.get<Project>(url);
  }

  /**
   * Get a dashboard by token
   *
   * @param {string} token The dashboard token
   * @returns {Observable<Project>} The dashboard as observable
   */
  getOneByToken(token: string): Observable<Project> {
    const url = `${projectsApiEndpoint}/project/${token}`;

    return this.httpClient.get<Project>(url);
  }


  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param {Project} project The project
   * @returns {Observable<Project>} The project as observable
   */
  createProject(project: Project): Observable<Project> {
    const url = `${projectsApiEndpoint}`;

    return this.httpClient.put<Project>(url, project)
      .pipe(
        map(projectAdded => {
          this.dashboardService.updateDashboardListSubject(projectAdded, this.dashboardService.dashboardActionUpdate);
          return projectAdded;
        })
      );
  }

  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param {Project} project The project
   * @returns {Observable<Project>} The project as observable
   */
  editProject(project: Project): Observable<Project> {
    const url = `${projectsApiEndpoint}/${project.id}`;

    return this.httpClient.put<Project>(url, project)
      .pipe(
        map(projectAdded => {
          this.dashboardService.updateDashboardListSubject(projectAdded, this.dashboardService.dashboardActionUpdate);
          return projectAdded;
        })
      );
  }

  /**
   * Add a new widget to the project
   *
   * @param {ProjectWidget} projectWidget The project widget to modify
   * @returns {Observable<Project>} The project as observable
   */
  addWidgetToProject(projectWidget: ProjectWidget): Observable<Project> {
    const url = `${projectsApiEndpoint}/${projectWidget.project.id}/widgets`;

    return this
      .httpClient
      .put<Project>(url, projectWidget)
      .pipe(
        map(project => {
          this.dashboardService.currentDisplayedDashboardValue = project;
          return project;
        })
      );
  }

  /**
   * Add a user to a project
   *
   * @param {Project} project The project
   * @param {string} username The username to add
   * @returns {Observable<Project>} The project as observable
   */
  addUserToProject(project: Project, username: string): Observable<Project> {
    const url = `${projectsApiEndpoint}/${project.id}/users`;

    return this.httpClient.put<Project>(url, username)
      .pipe(
        map(projectUpdated => {
          this.dashboardService.updateDashboardListSubject(project, this.dashboardService.dashboardActionUpdate);
          return projectUpdated;
        })
      );
  }

  /**
   * Delete a user from a project
   *
   * @param {Project} project The project
   * @param {number} userId The userId
   * @returns {Observable<Project>} The project as observable
   */
  deleteUserFromProject(project: Project, userId: number): Observable<Project> {
    const url = `${projectsApiEndpoint}/${project.id}/users/${userId}`;

    return this.httpClient.delete<Project>(url)
      .pipe(
        map(projectUpdated => {
          this.dashboardService.updateDashboardListSubject(project, this.dashboardService.dashboardActionUpdate);
          return projectUpdated;
        })
      );
  }

  /**
   * Delete a project
   *
   * @param {Project} project
   * @returns {Observable<Project>}
   */
  deleteProject(project: Project): Observable<Project> {
    const url = `${projectsApiEndpoint}/${project.id}`;

    return this.httpClient.delete<Project>(url)
      .pipe(
        map(projectDelete => {
          this.dashboardService.updateDashboardListSubject(projectDelete, this.dashboardService.dashboardActionDelete);
          return projectDelete;
        })
      );
  }

  /**
   * Update the list of project widgets position for a project
   *
   * @param {number} projectId The project id to update
   * @param {ProjectWidgetPosition[]} projectWidgetPositions The list of project widget position
   * @returns {Observable<Project>} The project updated
   */
  updateWidgetPositionForProject(projectId: number, projectWidgetPositions: ProjectWidgetPosition[]): Observable<Project> {
    const url = `${projectsApiEndpoint}/${projectId}/projectWidgetPositions`;

    return this.httpClient.put<Project>(url, projectWidgetPositions)
      .pipe(
        map(project => {
          this.dashboardService.updateDashboardListSubject(project, this.dashboardService.dashboardActionUpdate);
          return project;
        })
      );
  }

  /**
   * Delete a project widget from a dashboard
   *
   * @param {number} projectId The project id
   * @param {number} projectWidgetId The project widget id to delete
   * @returns {Observable<Project>} The project updated
   */
  deleteProjectWidgetFromProject(projectId: number, projectWidgetId: number): Observable<Project> {
    const url = `${projectsApiEndpoint}/${projectId}/projectWidgets/${projectWidgetId}`;

    return this.httpClient.delete<Project>(url)
      .pipe(
        map(project => {
          this.dashboardService.updateDashboardListSubject(project, this.dashboardService.dashboardActionUpdate);
          this.dashboardService.currentDisplayedDashboardValue = project;

          return project;
        })
      );
  }

  /**
   * Edit a project widget from a project
   *
   * @param {number} projectId The project id
   * @param {ProjectWidget} projectWidget The project widget to edit
   * @returns {Observable<Project>} The project updated
   */
  editProjectWidgetFromProject(projectId: number, projectWidget: ProjectWidget): Observable<Project> {
    const url = `${projectsApiEndpoint}/${projectId}/projectWidgets/${projectWidget.id}`;

    return this.httpClient.put<Project>(url, projectWidget);
  }
}