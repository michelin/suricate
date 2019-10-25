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

import { User } from '../../model/api/user/User';
import { usersApiEndpoint } from '../../../app.constant';
import { UserRequest } from '../../model/api/user/UserRequest';
import { UserSettingRequest } from '../../model/api/setting/UserSettingRequest';
import { UserSetting } from '../../model/api/setting/UserSetting';

/**
 * Manage the http user calls
 */
@Injectable({ providedIn: 'root' })
export class HttpUserService {
  /**
   * Constructor
   *
   * @param httpClient The http client
   */
  constructor(private httpClient: HttpClient) {}

  /**
   * Get the list of users
   *
   * @returns {Observable<User[]>} The list of users
   */
  getAll(filter: string = ''): Observable<User[]> {
    const url = `${usersApiEndpoint}?filter=${filter}`;

    return this.httpClient.get<User[]>(url);
  }

  /**
   * Get a user by id
   *
   * @param {number} userId The user id to find
   * @returns {Observable<User>} The user found
   */
  getById(userId: number): Observable<User> {
    const url = `${usersApiEndpoint}/${userId}`;

    return this.httpClient.get<User>(url);
  }

  /**
   * Update a user
   *
   * @param {number} userId The userId to update
   * @param userRequest The user request
   */
  updateUser(userId: number, userRequest: UserRequest): Observable<void> {
    const url = `${usersApiEndpoint}/${userId}`;

    return this.httpClient.put<void>(url, userRequest);
  }

  /**
   * Delete a user
   *
   * @param userId The user id to delete
   */
  deleteUser(userId: number): Observable<void> {
    const url = `${usersApiEndpoint}/${userId}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the user settings
   *
   * @param userId The user id
   */
  getUserSettings(userId: number): Observable<UserSetting[]> {
    const url = `${usersApiEndpoint}/${userId}/settings`;

    return this.httpClient.get<UserSetting[]>(url);
  }

  /**
   * Get a user setting by user id and setting id
   *
   * @param userId The user id
   * @param settingId The setting id
   */
  getUserSetting(userId: number, settingId: number): Observable<UserSetting> {
    const url = `${usersApiEndpoint}/${userId}/settings/${settingId}`;

    return this.httpClient.get<UserSetting>(url);
  }

  /**
   * Update user settings
   *
   * @param {number} userId The user id to update
   * @param {number} settingId The setting id
   * @param {UserSettingRequest} userSettingRequest The user setting request
   */
  updateUserSetting(userId: number, settingId: number, userSettingRequest: UserSettingRequest): Observable<void> {
    const url = `${usersApiEndpoint}/${userId}/settings/${settingId}`;

    return this.httpClient.put<void>(url, userSettingRequest);
  }

  /**
   * Get the connected user
   *
   * @returns {Observable<User>} The connected user
   */
  getConnectedUser(): Observable<User> {
    const url = `${usersApiEndpoint}/current`;

    return this.httpClient.get<User>(url);
  }
}
