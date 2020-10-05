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
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Role } from '../../../models/backend/role/role';
import { User } from '../../../models/backend/user/user';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';

/**
 * Manage the http role calls
 */
@Injectable({ providedIn: 'root' })
export class HttpRoleService {
  /**
   * Global roles endpoint
   * @type {string}
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
  public getRoles(filter?: HttpFilter): Observable<Page<Role>> {
    const url = `${HttpRoleService.rolesApiEndpoint}`;

    return this.httpClient.get<Page<Role>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a role by id
   *
   * @param roleId The role id
   */
  public getOneById(roleId: number): Observable<Role> {
    const url = `${HttpRoleService.rolesApiEndpoint}/${roleId}`;

    return this.httpClient.get<Role>(url);
  }

  /**
   * Get a list of users for a role
   *
   * @param roleId The role id
   */
  public getUsersByRole(roleId: number): Observable<User[]> {
    const url = `${HttpRoleService.rolesApiEndpoint}/${roleId}/users`;

    return this.httpClient.get<User[]>(url);
  }
}
