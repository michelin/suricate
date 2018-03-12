/*
 * Copyright 2012-2018 the original author or authors.
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
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {Project} from '../../shared/model/dto/Project';
import {Observable} from 'rxjs/Observable';
import {ProjectWidget} from '../../shared/model/dto/ProjectWidget';
import {map} from 'rxjs/operators/map';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class DashboardService extends AbstractHttpService {

  public static readonly PROJECTS_BASE_URL = `${AbstractHttpService.BASE_URL}/${AbstractHttpService.PROJECTS_URL}`;

  /**
   * Hold the list of dashboards
   *
   * @type {BehaviorSubject<Project[]>}
   */
  dashboardsSubject = new BehaviorSubject<Project[]>([]);
  currendDashbordSubject = new Subject<Project>();

  /**
   * The constructor
   *
   * @param {HttpClient} httpClient The http client
   */
  constructor(private httpClient: HttpClient) {
    super();
  }

  /**
   * Update the list hold by the subject
   *
   * @param {Project} project The project
   */
  private updateSubject(project: Project) {
    const currentList = this.dashboardsSubject.getValue();
    const indexOfCurrentProject = currentList.findIndex(currentProject => currentProject.id === project.id);

    if (indexOfCurrentProject >= 0) {
      currentList.splice(indexOfCurrentProject, 1);
    }

    this.dashboardsSubject.next([...currentList, project]);
  }

  /**
   * Get every dashboards and update the list
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  getAll(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${DashboardService.PROJECTS_BASE_URL}`).pipe(
      map(projects => {
        this.dashboardsSubject.next(projects);
        return projects;
      })
    );
  }

  /**
   * Get a dashboard by id
   *
   * @param {string} id The dashboard id
   * @returns {Observable<Project>} The dashboard as observable
   */
  getOneById(id: string): Observable<Project> {
    return this.httpClient.get<Project>(`${DashboardService.PROJECTS_BASE_URL}/${id}`);
  }

  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param {Project} project The project
   * @returns {Observable<Project>} The project as observable
   */
  saveProject(project: Project): Observable<Project> {
    return this.httpClient.put<Project>(`${DashboardService.PROJECTS_BASE_URL}`, project)
        .pipe(
          map(projectAdded => {
            this.updateSubject(projectAdded);
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
    return this.httpClient.put<Project>(`${DashboardService.PROJECTS_BASE_URL}/${projectWidget.projectId}`, projectWidget);
  }

  /**
   * Add a user to a project
   *
   * @param {Project} project The project
   * @param {string} username The username to add
   * @returns {Observable<Project>} The project as observable
   */
  addUserToProject(project: Project, username: string): Observable<Project> {
    return this.httpClient.put<Project>(`${DashboardService.PROJECTS_BASE_URL}/${project.id}/users/`, username)
        .pipe(
            map(projectUpdated => {
              this.updateSubject(project);
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
    return this.httpClient.delete<Project>(`${DashboardService.PROJECTS_BASE_URL}/${project.id}/users/${userId}`)
        .pipe(
            map(projectUpdated => {
              this.updateSubject(project);
              return projectUpdated;
            })
        );
  }
}
