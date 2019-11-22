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
import { Observable, Subject } from 'rxjs';
import { HttpProjectService } from '../../shared/services/backend/http-project.service';
import { map } from 'rxjs/operators';
import { AuthenticationService } from '../../shared/services/frontend/authentication.service';
import { NumberUtils } from '../../shared/utils/number.utils';

/**
 * The dashboard service, manage http calls
 */
@Injectable({ providedIn: 'root' })
export class DashboardService {
  /**
   * Define the min bound for the screen code random generation
   * @type {number}
   */
  private static readonly minScreenCodeBound = 100000;

  /**
   * Define the max bound for the screen code random generation
   * @type {number}
   */
  private static readonly maxScreenCodeBound = 999999;

  /**
   * Tell if the dashboard screen should refresh
   */
  private shouldRefreshProject = new Subject<boolean>();

  /**
   * Tell if the dashboard screen should refresh the project widget list
   */
  private shouldRefreshProjectWidgets = new Subject<boolean>();

  /**
   * The constructor
   */
  constructor(private httpProjectService: HttpProjectService) {}

  /**
   * Generate a random screen code
   */
  public static generateScreenCode(): number {
    return NumberUtils.getRandomIntBetween(this.minScreenCodeBound, this.maxScreenCodeBound);
  }

  /* ******* Event refresh dashboard screen **************** */

  /**
   * Event we should subscribe to receive event of refresh
   */
  refreshProjectEvent(): Observable<boolean> {
    return this.shouldRefreshProject.asObservable();
  }

  /**
   * Send an event for the refresh
   */
  refreshProject(): void {
    this.shouldRefreshProject.next(true);
  }

  /* ******* Event refresh the project widget list **************** */

  /**
   * Event we should subscribe to receive event of refresh
   */
  refreshProjectWidgetsEvent(): Observable<boolean> {
    return this.shouldRefreshProjectWidgets.asObservable();
  }

  /**
   * Send an event for the refresh
   */
  refreshProjectWidgets(): void {
    this.shouldRefreshProjectWidgets.next(true);
  }

  /* ******************************************************************* */
  /*                      Other Management                               */

  /* ******************************************************************* */

  /**
   * Check if the dashboard should be displayed without rights
   *
   * @param dashboardToken The dashboard token
   */
  shouldDisplayedReadOnly(dashboardToken: string): Observable<boolean> {
    return this.httpProjectService.getProjectUsers(dashboardToken).pipe(
      map(dashboardUsers => {
        return (
          !AuthenticationService.isAdmin() &&
          !dashboardUsers.some(user => user.username === AuthenticationService.getConnectedUser().username)
        );
      })
    );
  }
}
