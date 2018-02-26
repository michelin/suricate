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
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/share';

import { Login } from '../../shared/model/dto/Login';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';

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
   * Log the user by sending a request to the backend
   */
  login(user: Login): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('Authorization', 'Basic ' + btoa(user.username + ':' + user.password));
    headers = headers.append('Content-Type', 'application/x-www-form-urlencoded');

    return this.http
      .post<any>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.LOGIN_URL}`, null, {headers: headers})
      .map(response => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          this.loggedIn.next(true);

          return response;
        }
      });
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
}
