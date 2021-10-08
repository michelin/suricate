/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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
import { ActivatedRoute } from '@angular/router';

/**
 * Service used to manage global routes
 */
@Injectable({ providedIn: 'root' })
export class RoutesService {
  /**
   * Get the deeper activated route from the current route
   *
   * @param activatedRoute The activated route given
   */
  public static getDeeperActivatedRoute(activatedRoute: ActivatedRoute): ActivatedRoute {
    if (activatedRoute.firstChild) {
      return this.getDeeperActivatedRoute(activatedRoute.firstChild);
    }

    return activatedRoute;
  }

  /**
   * Get the param value store in the activated route
   *
   * @param activatedRoute The route activated by the component
   * @param paramName The name of the param to retrieve
   */
  public static getParamValueFromActivatedRoute(activatedRoute: ActivatedRoute, paramName: string): string {
    return activatedRoute.snapshot.params[paramName];
  }
}
