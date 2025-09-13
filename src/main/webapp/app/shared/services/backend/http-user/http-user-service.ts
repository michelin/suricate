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
import { PersonalAccessToken } from '../../../models/backend/personal-access-token/personal-access-token';
import { PersonalAccessTokenRequest } from '../../../models/backend/personal-access-token/personal-access-token-request';
import { UserSetting } from '../../../models/backend/setting/user-setting';
import { UserSettingRequest } from '../../../models/backend/setting/user-setting-request';
import { User } from '../../../models/backend/user/user';
import { UserRequest } from '../../../models/backend/user/user-request';
import { AbstractHttpService } from '../abstract-http/abstract-http-service';
import { HttpFilterService } from '../http-filter/http-filter-service';

/**
 * Manage the http user calls
 */
@Injectable({ providedIn: 'root' })
export class HttpUserService implements AbstractHttpService<User, UserRequest> {
	public static readonly usersApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/users`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get the list of users
	 * @returns The list of users
	 */
	public getAll(filter?: HttpFilter): Observable<PageModel<User>> {
		const url = `${HttpUserService.usersApiEndpoint}`;
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
	 * Create a user
	 */
	public create(userRequest: UserRequest): Observable<User> {
		const url = `${HttpUserService.usersApiEndpoint}`;

		return this.httpClient.post<User>(url, userRequest);
	}

	/**
	 * Update a user
	 * @param id The userId to update
	 * @param entity The user request
	 */
	public update(id: number, entity: UserRequest): Observable<void> {
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

	/**
	 * Get the user settings
	 * @param userName The username
	 */
	public getUserSettings(userName: string): Observable<UserSetting[]> {
		const url = `${HttpUserService.usersApiEndpoint}/${userName}/settings`;

		return this.httpClient.get<UserSetting[]>(url);
	}

	/**
	 * Update user settings
	 * @param userName The user to update
	 * @param settingId The setting id
	 * @param userSettingRequest The user setting request
	 */
	public updateUserSetting(
		userName: string,
		settingId: number,
		userSettingRequest: UserSettingRequest
	): Observable<void> {
		const url = `${HttpUserService.usersApiEndpoint}/${userName}/settings/${settingId}`;

		return this.httpClient.put<void>(url, userSettingRequest);
	}

	/**
	 * Get the current user tokens
	 */
	public getUserTokens(): Observable<PersonalAccessToken[]> {
		const url = `${HttpUserService.usersApiEndpoint}/personal-access-token`;
		return this.httpClient.get<PersonalAccessToken[]>(url);
	}

	/**
	 * Create a JWT token for the user
	 * @param tokenRequest The token request
	 */
	public createToken(tokenRequest: PersonalAccessTokenRequest): Observable<PersonalAccessToken> {
		const url = `${HttpUserService.usersApiEndpoint}/personal-access-token`;
		return this.httpClient.post<PersonalAccessToken>(url, tokenRequest);
	}

	/**
	 * Revoke a given token
	 * @param tokenName The token name
	 */
	public revokeToken(tokenName: string): Observable<void> {
		const url = `${HttpUserService.usersApiEndpoint}/personal-access-token/${tokenName}`;
		return this.httpClient.delete<void>(url);
	}
}
