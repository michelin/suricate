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
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {map} from 'rxjs/operators';

import {TokenService} from '../../../shared/auth/token.service';
import {UserSetting} from '../../../shared/model/api/UserSetting';
import {SettingsService} from '../../../shared/services/settings.service';
import {usersApiEndpoint} from '../../../app.constant';
import {User} from '../../../shared/model/api/user/User';
import {RoleEnum} from '../../../shared/model/api/enums/RoleEnum';

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
   * @param {HttpClient} httpClient The http client service to inject
   * @param {TokenService} tokenService The token service
   * @param {SettingsService} themeService The theme service
   */
  constructor(private httpClient: HttpClient,
              private tokenService: TokenService,
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
  /*                            HTTP Functions                                               */

  /* *************************************************************************************** */

  /**
   * Get the list of users
   *
   * @returns {Observable<User[]>} The list of users
   */
  getAll(): Observable<User[]> {
    const url = `${usersApiEndpoint}`;

    return this.httpClient.get<User[]>(url);
  }

  /**
   * Get a user by id
   *
   * @param {string} userId The user id to find
   * @returns {Observable<User>} The user found
   */
  getById(userId: string): Observable<User> {
    const url = `${usersApiEndpoint}/${userId}`;

    return this.httpClient.get<User>(url);
  }

  /**
   * Get the connected user
   *
   * @returns {Observable<User>} The connected user
   */
  getConnectedUser(): Observable<User> {
    const url = `${usersApiEndpoint}/current`;

    return this.httpClient.get<User>(url)
      .pipe(
        map(user => {
          this.connectedUser = user;
          return user;
        })
      );
  }

  /**
   * Delete a user
   * @param {User} user The user to delete
   */
  deleteUser(user: User): Observable<User> {
    const url = `${usersApiEndpoint}/${user.id}`;

    return this.httpClient.delete<User>(url);
  }

  /**
   * Update a user
   *
   * @param {User} user The user to update
   * @returns {Observable<User>} The user updated
   */
  updateUser(user: User): Observable<User> {
    const url = `${usersApiEndpoint}/${user.id}`;

    return this.httpClient.put<User>(url, user)
      .pipe(
        map(userUpdated => {
          if (userUpdated.id === this.connectedUser.id) {
            this.connectedUser = userUpdated;
          }
          return userUpdated;
        })
      );
  }

  /**
   * Update user settings
   *
   * @param {User} user The user to update
   * @param {UserSetting[]} userSettings The new settings
   * @returns {Observable<User>} The user updated
   */
  updateUserSettings(user: User, userSettings: UserSetting[]): Observable<User> {
    const url = `${usersApiEndpoint}/${user.id}/settings`;

    return this
      .httpClient
      .put<User>(url, userSettings)
      .pipe(
        map(userUpdated => {
          if (userUpdated.id === this.connectedUser.id) {
            this.connectedUser = userUpdated;
          }
          return userUpdated;
        })
      );
  }

  /**
   * Search a list of users by username
   *
   * @param {string} username The username to find
   * @returns {Observable<User[]>} The list of users that match the string
   */
  searchUserByUsername(username: string): Observable<User[]> {
    const url = `${usersApiEndpoint}/search?username=${username}`;

    return this.httpClient.get<User[]>(url);
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
