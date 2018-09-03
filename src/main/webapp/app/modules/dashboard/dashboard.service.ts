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
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {Project} from '../../shared/model/dto/Project';
import {ProjectWidget} from '../../shared/model/dto/ProjectWidget';
import {UserService} from '../security/user/user.service';
import {User} from '../../shared/model/dto/user/User';
import {ProjectWidgetPosition} from '../../shared/model/dto/ProjectWidgetPosition';
import {WidgetStateEnum} from '../../shared/model/dto/enums/WidgetSateEnum';
import {projectsApiEndpoint} from '../../app.constant';

/**
 * The dashboard service, manage http calls
 */
@Injectable()
export class DashboardService {

  /**
   * The action type DELETE
   * @type {string}
   * @private
   */
  private readonly dashboardActionDelete = 'DELETE';

  /**
   * The action type update
   * @type {string}
   * @private
   */
  private readonly dashboardActionUpdate = 'UPDATE';

  /**
   * Hold the list of the user dashboards
   * @type {BehaviorSubject<Project[]>}
   * @private
   */
  private userDashboardsSubject = new BehaviorSubject<Project[]>([]);

  /**
   * Hold the dashboard currently displayed to the user
   * @type {BehaviorSubject<Project>}
   * @private
   */
  private currendDashbordSubject = new BehaviorSubject<Project>(null);

  /**
   * The constructor
   *
   * @param {HttpClient} httpClient The http client
   * @param {UserService} userService The user service to inject
   */
  constructor(private httpClient: HttpClient,
              private userService: UserService) {
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
    return this.userDashboardsSubject.asObservable();
  }

  /**
   * Get the list of user dashboards
   * @returns {Observable<Project>}
   */
  get currentDashboardListValues(): Project[] {
    return this.userDashboardsSubject.getValue();
  }

  /**
   * Set and send the new list of current user dashboards
   * @param {Project[]} newProjectsList
   */
  set currentDashboardListValues(newProjectsList: Project[]) {
    this.userDashboardsSubject.next(newProjectsList);
  }

  /* ******* Current displayed Dashboard Management **************** */
  /**
   * Get the current displayed dashboard
   * @returns {Observable<Project>}
   */
  get currentDisplayedDashboard$(): Observable<Project> {
    return this.currendDashbordSubject.asObservable();
  }

  /**
   * Get the current displayed dashboard value
   * @returns {Project}
   */
  get currentDisplayedDashboardValue(): Project {
    return this.currendDashbordSubject.getValue();
  }

  /**
   * Set and send the new informations on the current displayed dashboard
   * @param {Project} project
   */
  set currentDisplayedDashboardValue(project: Project) {
    this.currendDashbordSubject.next(project);
  }

  /**
   * Update the list hold by the subject
   *
   * @param {Project} project The project
   * @param {string} action The action performed
   */
  private updateDashboardListSubject(project: Project, action: string): void {
    // Initialize search index
    let indexOfCurrentProject = -1;

    if (this.currentDashboardListValues != null) {
      indexOfCurrentProject = this.currentDashboardListValues.findIndex(currentProject => currentProject.id === project.id);
    }

    if (indexOfCurrentProject >= 0) {
      this.currentDashboardListValues.splice(indexOfCurrentProject, 1);
    }

    const currentUser: User = this.userService.connectedUser;
    if (project.users.findIndex(userLoop => userLoop.id === currentUser.id) >= 0) {
      if (action === this.dashboardActionUpdate) {
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
              this.updateDashboardListSubject(projectAdded, this.dashboardActionUpdate);
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
              this.updateDashboardListSubject(projectAdded, this.dashboardActionUpdate);
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
              this.currendDashbordSubject.next(project);
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
              this.updateDashboardListSubject(project, this.dashboardActionUpdate);
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
              this.updateDashboardListSubject(project, this.dashboardActionUpdate);
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
              this.updateDashboardListSubject(projectDelete, this.dashboardActionDelete);
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
              this.updateDashboardListSubject(project, this.dashboardActionUpdate);
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
              this.updateDashboardListSubject(project, this.dashboardActionUpdate);
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
    const url = `${projectsApiEndpoint}/${projectId}/projectWidgets/${projectWidget.id}`;

    return this.httpClient.put<Project>(url, projectWidget);
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
    if (projects === null) {
      return projects;
    }
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
