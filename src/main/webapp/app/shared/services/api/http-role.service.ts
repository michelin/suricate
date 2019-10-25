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

import { Role } from '../../model/api/role/Role';
import { rolesApiEndpoint } from '../../../app.constant';
import { User } from '../../model/api/user/User';

/**
 * Manage the http role calls
 */
@Injectable({ providedIn: 'root' })
export class HttpRoleService {
  /**
   * Constructor
   *
   * @param httpClient The http client service to inject
   */
  constructor(private httpClient: HttpClient) {}

  /**
   * Get the list of roles
   *
   * @returns {Observable<Role[]>}
   */
  getRoles(): Observable<Role[]> {
    const url = `${rolesApiEndpoint}`;

    return this.httpClient.get<Role[]>(url);
  }

  /**
   * Get a role by id
   *
   * @param roleId The role id
   */
  getOneById(roleId: number): Observable<Role> {
    const url = `${rolesApiEndpoint}/${roleId}`;

    return this.httpClient.get<Role>(url);
  }

  /**
   * Get a list of users for a role
   *
   * @param roleId The role id
   */
  getUsersByRole(roleId: number): Observable<User[]> {
    const url = `${rolesApiEndpoint}/${roleId}/users`;

    return this.httpClient.get<User[]>(url);
  }
}
