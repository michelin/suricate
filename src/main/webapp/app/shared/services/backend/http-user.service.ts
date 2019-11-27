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
import { EMPTY, Observable } from 'rxjs';

import { User } from '../../models/backend/user/user';
import { UserRequest } from '../../models/backend/user/user-request';
import { UserSettingRequest } from '../../models/backend/setting/user-setting-request';
import { UserSetting } from '../../models/backend/setting/user-setting';
import { AbstractHttpService } from './abstract-http.service';

/**
 * Manage the http user calls
 */
@Injectable({ providedIn: 'root' })
export class HttpUserService implements AbstractHttpService<User | UserRequest> {
  /**
   * Global endpoint for Users
   * @type {string}
   */
  public static readonly usersApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/users`;

  /**
   * Constructor
   *
   * @param httpClient The http client
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the list of users
   *
   * @returns {Observable<User[]>} The list of users
   */
  public getAll(filter: string = ''): Observable<User[]> {
    const url = `${HttpUserService.usersApiEndpoint}?filter=${filter}`;

    return this.httpClient.get<User[]>(url);
  }

  /**
   * Get a user by id
   *
   * @param {number} userId The user id to find
   * @returns {Observable<User>} The user found
   */
  public getById(userId: number): Observable<User> {
    const url = `${HttpUserService.usersApiEndpoint}/${userId}`;

    return this.httpClient.get<User>(url);
  }

  /**
   * Function used to create a new user
   *
   * @param entity The user to create
   */
  public create(entity: User | UserRequest): Observable<User> {
    return EMPTY;
  }

  /**
   * Update a user
   *
   * @param {number} id The userId to update
   * @param entity The user request
   */
  public update(id: number, entity: User | UserRequest): Observable<void> {
    const url = `${HttpUserService.usersApiEndpoint}/${id}`;

    return this.httpClient.put<void>(url, entity);
  }

  /**
   * Delete a user
   *
   * @param userId The user id to delete
   */
  public delete(userId: number): Observable<void> {
    const url = `${HttpUserService.usersApiEndpoint}/${userId}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Get the user settings
   *
   * @param userName The user name
   */
  public getUserSettings(userName: string): Observable<UserSetting[]> {
    const url = `${HttpUserService.usersApiEndpoint}/${userName}/settings`;

    return this.httpClient.get<UserSetting[]>(url);
  }

  /**
   * Get a user setting by user id and setting id
   *
   * @param userName The user name
   * @param settingId The setting id
   */
  public getUserSetting(userName: string, settingId: number): Observable<UserSetting> {
    const url = `${HttpUserService.usersApiEndpoint}/${userName}/settings/${settingId}`;

    return this.httpClient.get<UserSetting>(url);
  }

  /**
   * Update user settings
   *
   * @param {string} userName The user to update
   * @param {number} settingId The setting id
   * @param {UserSettingRequest} userSettingRequest The user setting request
   */
  public updateUserSetting(userName: string, settingId: number, userSettingRequest: UserSettingRequest): Observable<void> {
    const url = `${HttpUserService.usersApiEndpoint}/${userName}/settings/${settingId}`;

    return this.httpClient.put<void>(url, userSettingRequest);
  }

  /**
   * Get the connected user
   *
   * @returns {Observable<User>} The connected user
   */
  public getConnectedUser(): Observable<User> {
    const url = `${HttpUserService.usersApiEndpoint}/current`;

    return this.httpClient.get<User>(url);
  }
}
