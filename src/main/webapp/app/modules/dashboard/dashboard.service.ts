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
import {BehaviorSubject, Observable, Subject} from 'rxjs';

import {Project} from '../../shared/model/api/project/Project';
import {UserService} from '../security/user/user.service';

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
   * Tell if the dashboard screen should refresh
   */
  private shouldRefreshProjectWidgetList = new Subject<boolean>();

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

  /* ******* Event refresh dashboard screen list **************** */

  /**
   * Event we should subscribe to receive event of refresh
   */
  refreshProjectWidgetListEvent(): Observable<boolean> {
    return this.shouldRefreshProjectWidgetList.asObservable();
  }

  /**
   * Send an event for the refresh
   */
  refreshProjectWidgetList(): void {
    this.shouldRefreshProjectWidgetList.next(true);
  }
}
