/*
 * Copyright 2012-2021 the original author or authors.
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
import { Observable } from 'rxjs';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { map } from 'rxjs/operators';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { NumberUtils } from '../../../shared/utils/number.utils';

/**
 * The dashboard service
 */
@Injectable({ providedIn: 'root' })
export class DashboardService {
  /**
   * Define the min bound for the screen code random generation
   * @type {number}
   * @private
   */
  private static readonly minScreenCodeBound = 100000;

  /**
   * Define the max bound for the screen code random generation
   * @type {number}
   * @private
   */
  private static readonly maxScreenCodeBound = 999999;

  /**
   * Constructor
   *
   * @param {HttpProjectService} httpProjectService Suricate service used to manage http calls for project
   */
  constructor(private readonly httpProjectService: HttpProjectService) {}

  /**
   * Generate a random screen code
   */
  public static generateScreenCode(): number {
    return NumberUtils.getRandomIntBetween(this.minScreenCodeBound, this.maxScreenCodeBound);
  }

  /**
   * Check if the dashboard should be displayed without rights
   *
   * @param dashboardToken The dashboard token
   */
  public shouldDisplayedReadOnly(dashboardToken: string): Observable<boolean> {
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
