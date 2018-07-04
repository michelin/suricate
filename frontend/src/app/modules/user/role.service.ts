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
import {Observable} from 'rxjs/Observable';

import {Role} from '../../shared/model/dto/user/Role';
import {rolesApiEndpoint} from '../../app.constant';

/**
 * The role service
 */
@Injectable()
export class RoleService {

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The Http Client used for doing HTTP request
   */
  constructor(private httpClient: HttpClient) {
  }

  /* *************************************************************************************** */
  /*                            HTTP Functions                                               */

  /* *************************************************************************************** */

  /**
   * Get the list of roles
   *
   * @returns {Observable<Role[]>}
   */
  getRoles(): Observable<Role[]> {
    const url = `${rolesApiEndpoint}`;

    return this.httpClient.get<Role[]>(url);
  }


  /* *************************************************************************************** */
  /*                            Other Help Functions                                         */

  /* *************************************************************************************** */

  getRolesNameAsTable(roles: Role[]): string[] {
    const roleNames: string[] = [];
    roles.forEach(role => {
      if (role.name) {
        roleNames.push(role.name);
      }
    });

    return roleNames;
  }

  /**
   * Get the roles name for nested object management (display, sort)
   *
   * @param {Role[]} roles The list of roles to display
   * @returns {string} The list of roles has string
   */
  getRolesNameAsString(roles: Role[]): string {
    return roles.map(role => role.name).toString();
  }
}
