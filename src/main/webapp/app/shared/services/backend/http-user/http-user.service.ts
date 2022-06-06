/*
 * Copyright 2012-2021 the original author or authors.
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

import { User } from '../../../models/backend/user/user';
import { UserRequest } from '../../../models/backend/user/user-request';
import { UserSettingRequest } from '../../../models/backend/setting/user-setting-request';
import { UserSetting } from '../../../models/backend/setting/user-setting';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { Page } from '../../../models/backend/page';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';

/**
 * Manage the http user calls
 */
@Injectable({ providedIn: 'root' })
export class HttpUserService implements AbstractHttpService<User | UserRequest> {
  /**
   * Global endpoint for Users
   */
  public static readonly usersApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/users`;

  /**
   * Constructor
   * @param httpClient The http client
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the list of users
   *
   * @returns The list of users
   */
  public getAll(filter?: HttpFilter): Observable<Page<User>> {
    const url = `${HttpUserService.usersApiEndpoint}`;

    return this.httpClient.get<Page<User>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a user by id
   *
   * @param userId The user id to find
   * @returns The user found
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
}
