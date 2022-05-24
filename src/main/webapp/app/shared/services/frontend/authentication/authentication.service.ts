/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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
import { BehaviorSubject, Observable, ReplaySubject, Subject } from 'rxjs';
import { filter, tap } from 'rxjs/operators';
import { Credentials } from '../../../models/backend/user/credentials';
import { AuthenticationResponse } from '../../../models/backend/authentication/authentication-response';
import { User } from '../../../models/backend/user/user';
import { JwtHelperService } from '@auth0/angular-jwt';
import { AccessTokenDecoded } from '../../../models/frontend/token/access-token-decoded';
import { Role } from '../../../models/backend/role/role';
import { RoleEnum } from '../../../enums/role.enum';
import { UserRequest } from '../../../models/backend/user/user-request';
import { AbstractHttpService } from '../../backend/abstract-http/abstract-http.service';
import { HttpUserService } from '../../backend/http-user/http-user.service';
import { EnvironmentService } from '../environment/environment.service';
import { Page } from '../../../models/backend/page';
import { Project } from '../../../models/backend/project/project';
import { HttpFilterService } from '../../backend/http-filter/http-filter.service';

/**
 * The authentication service
 */
@Injectable({ providedIn: 'root' })
export class AuthenticationService {
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
  private static readonly authenticationApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/oauth/token`;

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
   * Hold the connected user
   */
  private connectedUser: BehaviorSubject<User> = new BehaviorSubject<User>(undefined);

  /**
   * Constructor
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
  public static setAccessToken(accessToken: string): void {
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
  /*private static decodeAccessToken(): AccessTokenDecoded {
    return AuthenticationService.jwtHelperService.decodeToken(AuthenticationService.getAccessToken());
  }*/

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
  public static setTokenType(tokenType: string): void {
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
  /*private static setRefreshToken(refreshToken: string): void {
    localStorage.setItem(AuthenticationService.localStorageRefreshTokenKey, refreshToken);
  }*/

  /**
   * Used to know if the user is currently logged in
   */
  public static isLoggedIn(): boolean {
    return AuthenticationService.getAccessToken() !== null && AuthenticationService.getAccessToken() !== '';
    // !AuthenticationService.isTokenExpired();
  }

  /**
   * Used to know if the token is expired or not
   */
  /*public static isTokenExpired(): boolean {
    return this.jwtHelperService.isTokenExpired(AuthenticationService.getAccessToken());
  }*/

  /**
   * Return the full token (token type + access token)
   */
  public static getFullToken(): string {
    return `${AuthenticationService.getTokenType()} ${AuthenticationService.getAccessToken()}`;
  }

  /**
   * Used to know if the connected user is admin or not
   */
  /*public static isAdmin(): boolean {
    return AuthenticationService.decodeAccessToken().authorities.includes(RoleEnum.ROLE_ADMIN);
  }*/

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
  /*public static getConnectedUser(): User {
    const decodedToken = AuthenticationService.decodeAccessToken();

    const user = new User();
    user.username = decodedToken.username;
    user.lastname = decodedToken.lastname;
    user.firstname = decodedToken.firstname;
    user.email = decodedToken.email;
    user.roles = decodedToken.authorities.map((roleEnum: RoleEnum) => {
      const role = new Role();
      role.name = roleEnum;
      return role;
    });

    return user;
  }*/

  /**
   * Get the current connected user
   */
  getConnectedUser(): Observable<User> {
    return this.connectedUser.asObservable().pipe(filter(user => user !== undefined));
  }

  /**
   * Set the current connected user
   * @param connectedUser The connected user
   */
  setConnectedUser(connectedUser: User): void {
    this.connectedUser.next(connectedUser);
  }

  /**
   * Authenticate the user throw OAuth2 Password grant
   *
   * @param credentials The user credentials
   * @returns The response as Observable
   */
  public authenticate(credentials: Credentials): Observable<AuthenticationResponse> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');
    headers = headers.append('Authorization', `Basic ${btoa('suricateAngular:suricateAngularSecret')}`);

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', credentials.username.toLowerCase());
    params.append('password', credentials.password);

    const url = `${AuthenticationService.authenticationApiEndpoint}`;

    return this.httpClient
      .post<AuthenticationResponse>(url, params.toString(), { headers: headers })
      .pipe(
        tap((authenticationResponse: AuthenticationResponse) => {
          if (authenticationResponse && authenticationResponse.access_token) {
            AuthenticationService.setTokenType(authenticationResponse.token_type);
            AuthenticationService.setAccessToken(authenticationResponse.access_token);
            // AuthenticationService.setRefreshToken(authenticationResponse.refresh_token);
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
  public register(userRequest: UserRequest): Observable<User> {
    const url = `${HttpUserService.usersApiEndpoint}/register`;

    return this.httpClient.post<User>(url, userRequest);
  }
}
