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
import {Observable} from 'rxjs';
import {User} from '../../model/api/user/User';
import {usersApiEndpoint} from '../../../app.constant';
import {map} from 'rxjs/operators';
import {UserService} from '../../../modules/security/user/user.service';
import {UserSetting} from '../../model/api/setting/UserSetting';

@Injectable()
export class HttpUserService {

  /**
   * Constructor
   *
   * @param httpClient The http client
   */
  constructor(private httpClient: HttpClient,
              private userService: UserService) {
  }

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
          this.userService.connectedUser = user;
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
          if (userUpdated.id === this.userService.connectedUser.id) {
            this.userService.connectedUser = userUpdated;
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
          if (userUpdated.id === this.userService.connectedUser.id) {
            this.userService.connectedUser = userUpdated;
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
}