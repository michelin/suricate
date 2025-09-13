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
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { Role } from '../../../enums/role';
import { AuthenticationResponse } from '../../../models/backend/authentication/authentication-response';
import { Role } from '../../../models/backend/role/role';
import { Credentials } from '../../../models/backend/user/credentials';
import { User } from '../../../models/backend/user/user';
import { UserRequest } from '../../../models/backend/user/user-request';
import { AccessTokenDecoded } from '../../../models/frontend/token/access-token-decoded';
import { AbstractHttpService } from '../../backend/abstract-http/abstract-http-service';
import { HttpUserService } from '../../backend/http-user/http-user-service';
import { EnvironmentService } from '../environment/environment-service';

/**
 * The authentication service
 */
@Injectable({ providedIn: 'root' })
export class AuthenticationService {
	private readonly httpClient = inject(HttpClient);

	/**
	 * OAuth2 URL
	 */
	public static readonly OAUTH2_URL = `${AbstractHttpService.baseApiEndpoint}/oauth2/authorization`;

	/**
	 * OAuth2 authentication with GitHub endpoint
	 */
	public static readonly GITHUB_AUTH_URL = `${AuthenticationService.OAUTH2_URL}/github?redirect_uri=${EnvironmentService.OAUTH2_FRONTEND_REDIRECT_URL}`;

	/**
	 * OAuth2 authentication with GitLab endpoint
	 */
	public static readonly GITLAB_AUTH_URL = `${AuthenticationService.OAUTH2_URL}/gitlab?redirect_uri=${EnvironmentService.OAUTH2_FRONTEND_REDIRECT_URL}`;

	/**
	 * Global endpoint for Authentication
	 */
	private static readonly authenticationApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/auth`;

	/**
	 * The access token local storage key
	 */
	private static readonly ACCESS_TOKEN_KEY = 'jwt';

	/**
	 * Auth0 service used to manage JWT with Angular
	 */
	private static readonly jwtHelperService = new JwtHelperService();

	/**
	 * Get the access token store in local storage
	 */
	public static getAccessToken(): string {
		return localStorage.getItem(AuthenticationService.ACCESS_TOKEN_KEY);
	}

	/**
	 * Set a new access token in local storage
	 * @param accessToken The access token to set
	 */
	public static setAccessToken(accessToken: string): void {
		localStorage.setItem(AuthenticationService.ACCESS_TOKEN_KEY, accessToken);
	}

	/**
	 * Remove the access token from the local storage
	 */
	private static removeAccessToken(): void {
		localStorage.removeItem(AuthenticationService.ACCESS_TOKEN_KEY);
	}

	/**
	 * Function used to decode the access token
	 */
	private static decodeAccessToken(): AccessTokenDecoded {
		return AuthenticationService.jwtHelperService.decodeToken(AuthenticationService.getAccessToken());
	}

	/**
	 * Used to know if the user is currently logged in
	 */
	public static isLoggedIn(): boolean {
		return !AuthenticationService.isTokenExpired();
	}

	/**
	 * Used to know if the token is expired or not
	 */
	public static isTokenExpired(): boolean {
		return this.jwtHelperService.isTokenExpired(AuthenticationService.getAccessToken());
	}

	/**
	 * Used to know if the connected user is admin or not
	 */
	public static isAdmin(): boolean {
		const token = AuthenticationService.decodeAccessToken();
		return token.roles && token.roles.includes(Role.ROLE_ADMIN);
	}

	/**
	 * Log out the user
	 */
	public static logout(): void {
		AuthenticationService.removeAccessToken();
	}

	/**
	 * Return the connected user with information store in the token
	 */
	public static getConnectedUser(): User {
		const decodedToken = AuthenticationService.decodeAccessToken();

		const user = new User();
		user.username = decodedToken.sub;
		user.lastname = decodedToken.lastname;
		user.firstname = decodedToken.firstname;
		user.email = decodedToken.email;
		user.avatarUrl = decodedToken.avatar_url;
		user.mode = decodedToken.mode;

		if (decodedToken.roles) {
			user.roles = decodedToken.roles.map((roleEnum: Role) => {
				const role = new Role();
				role.name = roleEnum;
				return role;
			});
		}

		return user;
	}

	/**
	 * Authenticate the user throw OAuth2 Password grant
	 *
	 * @param credentials The user credentials
	 * @returns The response as Observable
	 */
	public authenticate(credentials: Credentials): Observable<AuthenticationResponse> {
		const url = `${AuthenticationService.authenticationApiEndpoint}/signin`;

		return this.httpClient.post<AuthenticationResponse>(url, credentials).pipe(
			tap((authenticationResponse: AuthenticationResponse) => {
				if (authenticationResponse && authenticationResponse.accessToken) {
					AuthenticationService.setAccessToken(authenticationResponse.accessToken);
				}
			})
		);
	}

	/**
	 * Register a new user
	 *
	 * @param userRequest The user Request
	 * @returns The user registered
	 */
	public signup(userRequest: UserRequest): Observable<User> {
		const url = `${HttpUserService.usersApiEndpoint}/signup`;

		return this.httpClient.post<User>(url, userRequest);
	}
}
