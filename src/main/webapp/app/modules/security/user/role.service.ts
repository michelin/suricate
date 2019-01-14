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

import {Role} from '../../../shared/model/api/role/Role';

/**
 * The role service
 */
@Injectable()
export class RoleService {

  constructor() {
  }

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
    return roles.map(role => role.name).join(', ');
  }
}
