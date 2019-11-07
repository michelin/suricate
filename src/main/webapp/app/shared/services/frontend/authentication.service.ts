/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { authenticationApiEndpoint, usersApiEndpoint } from '../../../app.constant';
import { Credentials } from '../../models/backend/user/credentials';
import { AuthenticationResponse } from '../../models/backend/authentication/authentication-response';
import { User } from '../../models/backend/user/user';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AccessTokenDecoded } from '../../models/frontend/token/access-token-decoded';
import { Role } from '../../models/backend/role/role';
import { RoleEnum } from '../../enums/role.enum';
import { UserRequest } from '../../models/backend/user/user-request';

/**
 * The authentication service
 */
@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  /**
   * Auth0 service used to manage JWT with Angular
   */
  private static readonly jwtHelperService = new JwtHelperService();

  /**
   * The local storage key where the access token is store
   */
  private static readonly localStorageAccessTokenKey = 'suricate_access_token';
  /**
   * The local storage key where the refresh token is store
   */
  private static readonly localStorageRefreshTokenKey = 'suricate_refresh_token';
  /**
   * The local storage key where the token type is store
   */
  private static readonly localStorageTokenTypeKey = 'suricate_token_type';

  /**
   * Constructor
   *
   * @param httpClient Angular service used make http calls
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the access token store in local storage
   */
  private static getAccessToken(): string {
    return localStorage.getItem(AuthenticationService.localStorageAccessTokenKey);
  }
  /**
   * Set a new access token in local storage
   *
   * @param accessToken The access token to set
   */
  private static setAccessToken(accessToken: string): void {
    localStorage.setItem(AuthenticationService.localStorageAccessTokenKey, accessToken);
  }
  /**
   * Remove the access token from the local storage
   */
  private static removeAccessToken(): void {
    localStorage.removeItem(AuthenticationService.localStorageAccessTokenKey);
  }
  /**
   * Function used to decode the access token
   */
  private static decodeAccessToken(): AccessTokenDecoded {
    return AuthenticationService.jwtHelperService.decodeToken(AuthenticationService.getAccessToken());
  }

  /**
   * Return the token type stored in local storage
   */
  private static getTokenType(): string {
    return localStorage.getItem(AuthenticationService.localStorageTokenTypeKey);
  }
  /**
   * Set a new token type in local storage
   *
   * @param tokenType The type of token stored
   */
  private static setTokenType(tokenType: string): void {
    localStorage.setItem(AuthenticationService.localStorageTokenTypeKey, tokenType);
  }
  /**
   * Remove the token type from the local storage
   */
  private static removeTokenType(): void {
    localStorage.removeItem(AuthenticationService.localStorageTokenTypeKey);
  }
  /**
   * Set a new refresh token in local storage
   *
   * @param refreshToken The refresh token to set
   */
  private static setRefreshToken(refreshToken: string): void {
    localStorage.setItem(AuthenticationService.localStorageRefreshTokenKey, refreshToken);
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
   * Return the full token (token type + access token)
   */
  public static getFullToken(): string {
    return `${AuthenticationService.getTokenType()} ${AuthenticationService.getAccessToken()}`;
  }
  /**
   * Used to know if the connected user is admin or not
   */
  public static isAdmin(): boolean {
    return AuthenticationService.decodeAccessToken().authorities.includes(RoleEnum.ROLE_ADMIN);
  }
  /**
   * Used to logout the used
   */
  public static logout(): void {
    AuthenticationService.removeAccessToken();
    AuthenticationService.removeTokenType();
  }
  /**
   * Return the connected user with information store in the token
   */
  public static getConnectedUser(): User {
    const decodedToken = AuthenticationService.decodeAccessToken();

    const user = new User();
    user.username = decodedToken.user_name;
    user.roles = decodedToken.authorities.map((roleEnum: RoleEnum) => {
      const role = new Role();
      role.name = roleEnum;
      return role;
    });

    return user;
  }

  /**
   * Authenticate the user throw OAuth2 Password grant
   *
   * @param {Credentials} credentials The user credentials
   * @returns {Observable<AuthenticationResponse>} The response as Observable
   */
  public authenticate(credentials: Credentials): Observable<AuthenticationResponse> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');
    headers = headers.append('Authorization', `Basic ${btoa('suricateAngular:suricateAngularSecret')}`);

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', credentials.username);
    params.append('password', credentials.password);

    const url = `${authenticationApiEndpoint}`;

    return this.httpClient.post<AuthenticationResponse>(url, params.toString(), { headers: headers }).pipe(
      tap((authenticationResponse: AuthenticationResponse) => {
        if (authenticationResponse && authenticationResponse.access_token) {
          AuthenticationService.setTokenType(authenticationResponse.token_type);
          AuthenticationService.setAccessToken(authenticationResponse.access_token);
          AuthenticationService.setRefreshToken(authenticationResponse.refresh_token);
        }
      })
    );
  }

  /**
   * Register a new user
   *
   * @param userRequest The user Request
   * @returns {Observable<User>} The user registered
   */
  register(userRequest: UserRequest): Observable<User> {
    const url = `${usersApiEndpoint}/register`;

    return this.httpClient.post<User>(url, userRequest);
  }
}
