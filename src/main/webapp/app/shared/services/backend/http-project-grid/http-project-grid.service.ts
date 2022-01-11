/*
 *
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
import { Observable } from 'rxjs';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpClient } from '@angular/common/http';
import { ProjectGrid } from '../../../models/backend/project/project-grid';

@Injectable({ providedIn: 'root' })
export class HttpProjectGridService {
  /**
   * Global endpoint for project grids
   */
  private static readonly projectGridsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/projectGrids`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Update all given grids of a project
   *
   * @param projectToken The project token
   * @param grids The project grids to update
   */
  public updateAll(projectToken: string, grids: ProjectGrid[]): Observable<void> {
    const url = `${HttpProjectGridService.projectGridsApiEndpoint}/${projectToken}`;

    return this.httpClient.put<void>(url, grids);
  }

  /**
   * Delete a given grid of a project
   *
   * @param projectToken The project token
   * @param gridId The grid id
   */
  public delete(projectToken: string, gridId: number): Observable<void> {
    const url = `${HttpProjectGridService.projectGridsApiEndpoint}/${projectToken}/${gridId}`;

    return this.httpClient.delete<void>(url);
  }
}
