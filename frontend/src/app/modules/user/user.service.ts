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


@Injectable()
export class UserService extends AbstractHttpService {

  constructor(private http: HttpClient) {
    super();
  }

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}`);
  }

  getById(userId: string): Observable<User> {
    return this.http.get<User>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}/${userId}`);
  }

  getConnectedUser(): Observable<User> {
    return this.http.get<User>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}/current`);
  }

  getUserInitial(user: User): string {
    return `${user.firstname.substring(0, 1)} ${user.lastname.substring(0, 1)}`;
  }
}
