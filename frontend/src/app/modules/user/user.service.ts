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
import { HttpClient } from '@angular/common/http';
import { User } from '../../shared/model/dto/user/User';

import { Observable } from 'rxjs/Observable';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {map} from 'rxjs/operators';
import {Subject} from 'rxjs/Subject';

/**
 * User service that manage users
 */
@Injectable()
export class UserService extends AbstractHttpService {

  /**
   * The base url for user API
   * @type {string} The user API url
   */
  public static readonly USERS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.USERS_URL}`;

  /**
   * The connected user subject
   *
   * @type {Subject<User>}
   */
  connectedUserSubject: Subject<User> = new Subject<User>();

  /**
   * The constructor
   *
   * @param {HttpClient} http The http client service to inject
   */
  constructor(private http: HttpClient) {
    super();
  }

  /**
   * Get the list of users
   *
   * @returns {Observable<User[]>} The list of users
   */
  getAll(): Observable<User[]> {
    return this.http.get<User[]>(`${UserService.USERS_BASE_URL}`);
  }

  /**
   * Get a user by id
   *
   * @param {string} userId The user id to find
   * @returns {Observable<User>} The user found
   */
  getById(userId: string): Observable<User> {
    return this.http.get<User>(`${UserService.USERS_BASE_URL}/${userId}`);
  }

  /**
   * Get the connected user
   *
   * @returns {Observable<User>} The connected user
   */
  getConnectedUser(): Observable<User> {
    return this.http.get<User>(`${UserService.USERS_BASE_URL}/current`).pipe(
        map(user => {
          this.connectedUserSubject.next(user);
          return user;
        })
    );
  }

  /**
   * Delete a user
   * @param {User} user The user to delete
   */
  deleteUser(user: User): Observable<User> {
    return this.http.delete<User>(`${UserService.USERS_BASE_URL}/${user.id}`);
  }

  /**
   * Search a list of users by username
   *
   * @param {string} username The username to find
   * @returns {Observable<User[]>} The list of users that match the string
   */
  searchUserByUsername(username: string): Observable<User[]> {
    return this.http.get<User[]>(`${UserService.USERS_BASE_URL}/search?username=${username}`);
  }

  /**
   * Get the initials of a user
   *
   * @param {User} user The user
   * @returns {string} The initials
   */
  getUserInitial(user: User): string {
    return `${user.firstname.substring(0, 1)}${user.lastname.substring(0, 1)}`;
  }
}
