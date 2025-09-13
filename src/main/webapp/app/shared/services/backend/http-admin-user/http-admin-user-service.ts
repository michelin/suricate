/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { User } from '../../../models/backend/user/user';
import { UserRequest } from '../../../models/backend/user/user-request';
import { AbstractHttpService } from '../abstract-http/abstract-http-service';
import { HttpFilterService } from '../http-filter/http-filter-service';
import { HttpUserService } from '../http-user/http-user-service';

@Injectable({ providedIn: 'root' })
export class HttpAdminUserService implements AbstractHttpService<User, UserRequest> {
	public static readonly adminUsersApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/admin/users`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get the list of users
	 * @returns The list of users
	 */
	public getAll(filter?: HttpFilter): Observable<PageModel<User>> {
		const url = `${HttpAdminUserService.adminUsersApiEndpoint}`;

		return this.httpClient.get<PageModel<User>>(HttpFilterService.getFilteredUrl(url, filter));
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
	 */
	public create(userRequest: UserRequest): Observable<User> {
		const url = `${HttpUserService.usersApiEndpoint}`;

		return this.httpClient.post<User>(url, userRequest);
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
