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
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { Page } from '../../../models/backend/page';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { UserRequest } from '../../../models/backend/user/user-request';
import { HttpUserService } from '../http-user/http-user.service';

@Injectable({ providedIn: 'root' })
export class HttpAdminUserService implements AbstractHttpService<User, UserRequest> {
  public static readonly adminUsersApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/admin/users`;

  /**
   * Constructor
   * @param httpClient The http client
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the list of users
   * @returns The list of users
   */
  public getAll(filter?: HttpFilter): Observable<Page<User>> {
    const url = `${HttpAdminUserService.adminUsersApiEndpoint}`;
    return this.httpClient.get<Page<User>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get a user by id
   * @param userId The user id to find
   * @returns The user found
   */
  public getById(userId: number): Observable<User> {
    const url = `${HttpUserService.usersApiEndpoint}/${userId}`;

    return this.httpClient.get<User>(url);
  }

  /**
   * Function used to create a new user
   * @param entity The user to create
   */
  public create(entity: User | UserRequest): Observable<User> {
    return EMPTY;
  }

  /**
   * Update a user
   * @param {number} id The userId to update
   * @param entity The user request
   */
  public update(id: number, entity: User | UserRequest): Observable<void> {
    const url = `${HttpUserService.usersApiEndpoint}/${id}`;

    return this.httpClient.put<void>(url, entity);
  }

  /**
   * Delete a user
   * @param userId The user id to delete
   */
  public delete(userId: number): Observable<void> {
    const url = `${HttpUserService.usersApiEndpoint}/${userId}`;

    return this.httpClient.delete<void>(url);
  }
}
