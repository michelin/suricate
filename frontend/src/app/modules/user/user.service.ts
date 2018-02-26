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
import { User } from '../../shared/model/dto/user/User';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {AbstractHttpService} from '../../shared/services/abstract-http.service';


@Injectable()
export class UserService extends AbstractHttpService {

  constructor(private http: HttpClient) {
    super();
  }

  getAll(): Observable<User[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');

    return this.http
        .get<User[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}`, {headers: headers})
        .map(response => AbstractHttpService.extractData(response))
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }

  getById(userId: string): Observable<User> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');

    return this.http
        .get<User>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}/${userId}`, {headers: headers})
        .map(response => AbstractHttpService.extractData(response))
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }

  getConnectedUser(): Observable<User> {
    return this.http
        .get<User>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}/current`)
        .map(response => AbstractHttpService.extractData(response))
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }

  updateUser(user: User): void {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');

    const body = JSON.stringify(user);
    this.http
        .patch<User>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.USER_URL}/update`, body , {headers: headers})
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }

  getUserInitial(user: User): string {
    let initial = '';
    initial = initial.concat(user.fullname.split(' ')[0].substring(0, 1));
    initial = initial.concat(user.fullname.split(' ')[1].substring(0, 1));

    return initial;
  }
}
