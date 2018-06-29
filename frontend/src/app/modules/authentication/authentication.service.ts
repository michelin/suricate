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

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {map} from 'rxjs/operators';

import {Credentials} from '../../shared/model/dto/user/Credentials';
import {AuthenticationResponse} from '../../shared/model/dto/user/AuthenticationResponse';
import {User} from '../../shared/model/dto/user/User';
import {TokenService} from '../../shared/auth/token.service';
import {UserService} from '../user/user.service';
import {authenticationApiEndpoint, usersApiEndpoint} from '../../app.constant';

/**
 * The authentication service
 */
@Injectable()
export class AuthenticationService {
  /**
   * LoggedIn Subject (Hold if the user is logged in or not)
   * @type {BehaviorSubject<boolean>}
   * @private
   */
  private _loggedInSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(this._tokenService.hasToken());

  /**
   * Constructor
   *
   * @param {HttpClient} _httpClient The HttpClient service
   * @param {TokenService} _tokenService The token service
   * @param {UserService} _userService The user service
   */
  constructor(private _httpClient: HttpClient,
              private _tokenService: TokenService,
              private _userService: UserService) {
  }

  /* ******************************************************************* */
  /*                      Subject Management Part                        */

  /* ******************************************************************* */

  /**
   * Tell if the user is log or not
   *
   * @returns {Observable<boolean>}
   */
  get isLoggedIn$(): Observable<boolean> {
    return this._loggedInSubject.asObservable();
  }

  /**
   * Set and send if the user is logged in or not
   * @param {boolean} isLoggedIn
   */
  set isLoggedIn(isLoggedIn: boolean) {
    this._loggedInSubject.next(isLoggedIn);
  }

  /* ******************************************************************* */
  /*                 Authentication HTTP Management                      */

  /* ******************************************************************* */

  /**
   * Authenticate the user throw OAuth2 Password grant
   *
   * @param {Credentials} credentials The user credentials
   * @returns {Observable<AuthenticationResponse>} The response as Observable
   */
  authenticate(credentials: Credentials): Observable<AuthenticationResponse> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');
    headers = headers.append('Authorization', `Basic ${btoa('suricateAngular:suricateAngularSecret')}`);

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', credentials.username);
    params.append('password', credentials.password);

    const url = `${authenticationApiEndpoint}`;

    return this._httpClient
        .post<AuthenticationResponse>(url, params.toString(), {headers: headers})
        .pipe(
            map(authenticationResponse => {
              if (authenticationResponse && authenticationResponse.access_token) {
                this._tokenService.token = authenticationResponse.access_token;
                this.isLoggedIn = true;

                return authenticationResponse;
              }
            })
        );
  }

  /**
   * Register a new user
   *
   * @param {User} user The user to register
   * @returns {Observable<User>} The user registered
   */
  register(user: User): Observable<User> {
    const url = `${usersApiEndpoint}/register`;

    return this._httpClient.post<User>(url, user);
  }

  /**
   * Logout the user
   */
  logout(): void {
    // clear token remove user from local storage to log user out
    localStorage.removeItem('token');
    this.isLoggedIn = false;
    this._userService.connectedUser = null;
  }
}
