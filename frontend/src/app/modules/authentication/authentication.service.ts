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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import {catchError, map} from 'rxjs/operators';

import { ICredentials } from '../../shared/model/dto/user/ICredentials';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {AuthenticationResponse} from '../../shared/model/dto/user/AuthenticationResponse';

@Injectable()
export class AuthenticationService extends AbstractHttpService {
  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(AuthenticationService.hasToken());

  constructor(private http: HttpClient) {
    super();
  }

  static getToken(): String {
    const token = localStorage.getItem('token');
    return token ? token : '';
  }

  /**
   * if we have token the user is loggedIn
   * @returns {boolean}
   */
  static hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Tell if the user is log or not
   *
   * @returns {Observable<boolean>}
   */
  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  logout(): void {
    // clear token remove user from local storage to log user out
    localStorage.removeItem('token');
    this.loggedIn.next(false);
  }

  /**
   * Log the user by sending a request to the backend
   */
  authenticate(credentials: ICredentials): Observable<AuthenticationResponse> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');
    headers = headers.append('Authorization', `Basic ${btoa('suricateAngular:suricateAngularSecret')}`);

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', credentials.username);
    params.append('password', credentials.password);

    const url = `${AbstractHttpService.BASE_URL}/${AbstractHttpService.AUTHENTICATE_URL}`;

    return this.http
      .post<AuthenticationResponse>(url, params.toString(), {headers: headers})
      .pipe(
          map(authenticationResponse => {
            if (authenticationResponse && authenticationResponse.access_token) {
              localStorage.setItem('token', authenticationResponse.access_token);
              this.loggedIn.next(true);

              return authenticationResponse;
            }
          }),
          catchError(error => AbstractHttpService.handleErrorObservable(error))
      );
  }
}
