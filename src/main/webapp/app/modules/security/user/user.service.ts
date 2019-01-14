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
import {BehaviorSubject, Observable} from 'rxjs';

import {TokenService} from '../../../shared/auth/token.service';
import {SettingsService} from '../../settings/settings.service';
import {User} from '../../../shared/model/api/user/User';
import {RoleEnum} from '../../../shared/model/enums/RoleEnum';

/**
 * User service that manage users
 */
@Injectable()
export class UserService {

  /**
   * The connected user subject
   * @type {Subject<User>}
   */
  private connectedUserSubject: BehaviorSubject<User> = new BehaviorSubject<User>(null);

  /**
   * The constructor
   *
   * @param {TokenService} tokenService The token service
   * @param {SettingsService} themeService The theme service
   */
  constructor(private tokenService: TokenService,
              private themeService: SettingsService) {
  }

  /* *************************************************************************************** */
  /*                            Subject Management                                           */

  /* *************************************************************************************** */

  /**
   * Get the connected user event
   * @returns {Observable<User>}
   */
  get connectedUser$(): Observable<User> {
    return this.connectedUserSubject.asObservable();
  }

  /**
   * Get the current connected user value
   * @returns {User}
   */
  get connectedUser(): User {
    return this.connectedUserSubject.getValue();
  }

  /**
   * Set and send the new connected user
   * @param {User} connectedUser
   */
  set connectedUser(connectedUser: User) {
    this.connectedUserSubject.next(connectedUser);
  }

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
    return `${user.firstname.substring(0, 1)}${user.lastname.substring(0, 1)}`;
  }

  /**
   * Test if the user is admin
   * @returns {boolean} True if the current user admin, false otherwise
   */
  isAdmin(): boolean {
    return this.tokenService.getUserRoles().includes(RoleEnum.ROLE_ADMIN);
  }
}
