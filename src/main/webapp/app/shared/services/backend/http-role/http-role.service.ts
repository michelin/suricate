/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { Role } from '../../../models/backend/role/role';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilterService } from '../http-filter/http-filter.service';

/**
 * Manage the http role calls
 */
@Injectable({ providedIn: 'root' })
export class HttpRoleService {
  /**
   * Global roles endpoint
   */
  private static readonly rolesApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/roles`;

  /**
   * Constructor
   *
   * @param httpClient The http client service to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the list of roles
   *
   * @returns {Observable<Role[]>}
   */
  public getRoles(filter?: HttpFilter): Observable<PageModel<Role>> {
    const url = `${HttpRoleService.rolesApiEndpoint}`;

    return this.httpClient.get<PageModel<Role>>(HttpFilterService.getFilteredUrl(url, filter));
  }
}
