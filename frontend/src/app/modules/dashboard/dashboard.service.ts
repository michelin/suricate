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

import {Injectable} from '@angular/core';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {Project} from '../../shared/model/dto/Project';
import {Observable} from 'rxjs/Observable';
import {ProjectWidget} from '../../shared/model/dto/ProjectWidget';
import {map} from 'rxjs/operators/map';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {UserService} from '../user/user.service';
import {User} from '../../shared/model/dto/user/User';
import {ProjectWidgetPosition} from '../../shared/model/dto/ProjectWidgetPosition';
import {WidgetStateEnum} from '../../shared/model/dto/enums/WidgetSateEnum';

/**
 * The dashboard service, manage http calls
 */
@Injectable()
export class DashboardService extends AbstractHttpService {

  /**
   * The action type DELETE
   * @type {string}
   * @private
   */
  private static readonly _ACTION_DELETE = 'DELETE';
  /**
   * The action type update
   * @type {string}
   * @private
   */
  private static readonly _ACTION_UPDATE = 'UPDATE';

  /**
   * The base project url for http calls
   * @type {string}
   * @private
   */
  private static readonly _PROJECTS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.PROJECTS_URL}`;

  /**
   * Hold the list of the user dashboards
   * @type {BehaviorSubject<Project[]>}
   * @private
   */
  private _userDashboardsSubject = new BehaviorSubject<Project[]>([]);

  /**
   * Hold the dashboard currently displayed to the user
   * @type {BehaviorSubject<Project>}
   * @private
   */
  private _currendDashbordSubject = new BehaviorSubject<Project>(null);

  /**
   * The constructor
   *
   * @param {HttpClient} _httpClient The http client
   * @param {UserService} _userService The user service to inject
   */
  constructor(private _httpClient: HttpClient,
              private _userService: UserService) {
    super();
  }

  /* ******************************************************************* */
  /*                      Subject Management Part                        */

  /* ******************************************************************* */

  /* ******* User Dashboards List Management **************** */
  /**
   * Get the list of user dashboards
   * @returns {Observable<Project>}
   */
  get currentDashboardList$(): Observable<Project[]> {
    return this._userDashboardsSubject.asObservable();
  }

  /**
   * Get the list of user dashboards
   * @returns {Observable<Project>}
   */
  get currentDashboardListValues(): Project[] {
    return this._userDashboardsSubject.getValue();
  }

  /**
   * Set and send the new list of current user dashboards
   * @param {Project[]} newProjectsList
   */
  set currentDashboardListValues(newProjectsList: Project[]) {
    this._userDashboardsSubject.next(newProjectsList);
  }

  /* ******* Current displayed Dashboard Management **************** */
  /**
   * Get the current displayed dashboard
   * @returns {Observable<Project>}
   */
  get currentDisplayedDashboard$(): Observable<Project> {
    return this._currendDashbordSubject.asObservable();
  }

  /**
   * Get the current displayed dashboard value
   * @returns {Project}
   */
  get currentDisplayedDashboardValue(): Project {
    return this._currendDashbordSubject.getValue();
  }

  /**
   * Set and send the new informations on the current displayed dashboard
   * @param {Project} project
   */
  set currentDisplayedDashboardValue(project: Project) {
    this._currendDashbordSubject.next(project);
  }

  /**
   * Update the list hold by the subject
   *
   * @param {Project} project The project
   * @param {string} action The action performed
   */
  private updateDashboardListSubject(project: Project, action: string): void {
    const indexOfCurrentProject = this.currentDashboardListValues.findIndex(currentProject => currentProject.id === project.id);

    if (indexOfCurrentProject >= 0) {
      this.currentDashboardListValues.splice(indexOfCurrentProject, 1);
    }

    const currentUser: User = this._userService.connectedUser;
    if (project.users.findIndex(userLoop => userLoop.id === currentUser.id) >= 0) {
      if (action === DashboardService._ACTION_UPDATE) {
        this.currentDashboardListValues = [...this.currentDashboardListValues, project];
      } else {
        this.currentDashboardListValues = [...this.currentDashboardListValues];
      }
    }
  }

  /**
   * Use to update the html of a websocket
   *
   * @param {number} projectWidgetId The project widget ID to update
   * @param {string} newHtmlContent The HTML to replace
   * @param {WidgetStateEnum} widgetStatus The widget status
   */
  public updateWidgetHtmlFromProjetWidgetId(projectWidgetId: number, newHtmlContent: string, widgetStatus: WidgetStateEnum): void {
    let projectWidgetToUpdate: ProjectWidget = null;

    this.currentDisplayedDashboardValue.projectWidgets.filter(currentProjectWidget => {
      if (currentProjectWidget.id === projectWidgetId) {
        projectWidgetToUpdate = currentProjectWidget;
      }
    });

    if (projectWidgetToUpdate) {
      projectWidgetToUpdate.instantiateHtml = newHtmlContent;
      projectWidgetToUpdate.state = widgetStatus;
    }
  }

  /* ******************************************************************* */
  /*                      Dashboard HTTP Management                      */

  /* ******************************************************************* */

  /**
   * Get every dashboards and update the list
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  getAll(): Observable<Project[]> {
    return this._httpClient.get<Project[]>(`${DashboardService._PROJECTS_BASE_URL}`);
  }

  /**
   * Get every dashboards for the current user
   *
   * @returns {Observable<Project[]>} The list as observable
   */
  getAllForCurrentUser(): Observable<Project[]> {
    return this._httpClient.get<Project[]>(`${DashboardService._PROJECTS_BASE_URL}/currentUser`)
        .pipe(
            map(projects => {
              this.currentDashboardListValues = projects;
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
    return this._httpClient.get<Project>(`${DashboardService._PROJECTS_BASE_URL}/${id}`);
  }

  /**
   * Get a dashboard by token
   *
   * @param {string} token The dashboard token
   * @returns {Observable<Project>} The dashboard as observable
   */
  getOneByToken(token: string): Observable<Project> {
    return this._httpClient.get<Project>(`${DashboardService._PROJECTS_BASE_URL}/project/${token}`);
  }

  /**
   * Add/Update a dashboard and update the subject list
   *
   * @param {Project} project The project
   * @returns {Observable<Project>} The project as observable
   */
  createProject(project: Project): Observable<Project> {
    return this._httpClient.put<Project>(`${DashboardService._PROJECTS_BASE_URL}`, project)
        .pipe(
            map(projectAdded => {
              this.updateDashboardListSubject(projectAdded, DashboardService._ACTION_UPDATE);
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
    return this._httpClient.put<Project>(`${DashboardService._PROJECTS_BASE_URL}/${project.id}`, project)
        .pipe(
            map(projectAdded => {
              this.updateDashboardListSubject(projectAdded, DashboardService._ACTION_UPDATE);
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
    return this
        ._httpClient
        .put<Project>(`${DashboardService._PROJECTS_BASE_URL}/${projectWidget.project.id}/widgets`, projectWidget)
        .pipe(
            map(project => {
              this._currendDashbordSubject.next(project);
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
    return this._httpClient.put<Project>(`${DashboardService._PROJECTS_BASE_URL}/${project.id}/users`, username)
        .pipe(
            map(projectUpdated => {
              this.updateDashboardListSubject(project, DashboardService._ACTION_UPDATE);
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
    return this._httpClient.delete<Project>(`${DashboardService._PROJECTS_BASE_URL}/${project.id}/users/${userId}`)
        .pipe(
            map(projectUpdated => {
              this.updateDashboardListSubject(project, DashboardService._ACTION_UPDATE);
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
    return this._httpClient.delete<Project>(`${DashboardService._PROJECTS_BASE_URL}/${project.id}`)
        .pipe(
            map(projectDelete => {
              this.updateDashboardListSubject(projectDelete, DashboardService._ACTION_DELETE);
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
    const url = `${DashboardService._PROJECTS_BASE_URL}/${projectId}/projectWidgetPositions`;

    return this._httpClient.put<Project>(url, projectWidgetPositions)
        .pipe(
            map(project => {
              this.updateDashboardListSubject(project, DashboardService._ACTION_UPDATE);
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
    const url = `${DashboardService._PROJECTS_BASE_URL}/${projectId}/projectWidgets/${projectWidgetId}`;

    return this._httpClient.delete<Project>(url)
        .pipe(
            map(project => {
              this.updateDashboardListSubject(project, DashboardService._ACTION_UPDATE);
              this.currentDisplayedDashboardValue = project;

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
    const url = `${DashboardService._PROJECTS_BASE_URL}/${projectId}/projectWidgets/${projectWidget.id}`;

    return this._httpClient.put<Project>(url, projectWidget);
  }

  /* ******************************************************************* */
  /*                      Dashboard Other Management                     */

  /* ******************************************************************* */

  /**
   * Sort list of project by name
   *
   * @param {Project[]} projects The list of projects to sort
   */
  sortByProjectName(projects: Project[]): Project[] {
    return projects.sort((left, right): number => {
      if (left.name < right.name) {
        return -1;
      }
      if (left.name > right.name) {
        return 1;
      }
      return 0;
    });
  }
}
