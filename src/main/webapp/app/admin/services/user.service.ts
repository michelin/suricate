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

import { User } from '../../shared/models/backend/user/user';

/**
 * User service that manage users
 */
@Injectable({ providedIn: 'root' })
export class UserService {
  /**
   * The constructor
   */
  constructor() {}

  /* *************************************************************************************** */
  /*                            Other Help Functions                                         */

  /* *************************************************************************************** */

  /**
   * Get the initials of a user
   *
   * @param {User} user The user
   * @returns {string} The initials
   */
  getUserInitial(user: User): string {
    if (user.firstname && user.lastname) {
      return `${user.firstname.substring(0, 1)}${user.lastname.substring(0, 1)}`;
    }

    return '';
  }
}
