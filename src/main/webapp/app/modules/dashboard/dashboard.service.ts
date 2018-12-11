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
import {BehaviorSubject, Observable} from 'rxjs';

import {Project} from '../../shared/model/api/Project';
import {ProjectWidget} from '../../shared/model/api/ProjectWidget';
import {UserService} from '../security/user/user.service';
import {User} from '../../shared/model/api/user/User';
import {WidgetStateEnum} from '../../shared/model/api/enums/WidgetSateEnum';

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
  readonly dashboardActionDelete = 'DELETE';

  /**
   * The action type update
   * @type {string}
   * @private
   */
  readonly dashboardActionUpdate = 'UPDATE';

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
  private currentDashboardSubject = new BehaviorSubject<Project>(null);

  /**
   * The constructor
   *
   * @param {UserService} userService The user service to inject
   */
  constructor(private userService: UserService) {
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
    return this.currentDashboardSubject.asObservable();
  }

  /**
   * Get the current displayed dashboard value
   * @returns {Project}
   */
  get currentDisplayedDashboardValue(): Project {
    return this.currentDashboardSubject.getValue();
  }

  /**
   * Set and send the new informations on the current displayed dashboard
   * @param {Project} project
   */
  set currentDisplayedDashboardValue(project: Project) {
    this.currentDashboardSubject.next(project);
  }

  /**
   * Update the list hold by the subject
   *
   * @param {Project} project The project
   * @param {string} action The action performed
   */
  updateDashboardListSubject(project: Project, action: string): void {
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
      if (this.currentDashboardListValues == undefined) {
        this.currentDashboardListValues = [project];
      } else if (action === this.dashboardActionUpdate) {
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
