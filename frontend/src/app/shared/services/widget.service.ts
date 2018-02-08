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
import {AbstractHttpService} from './abstract-http.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Category} from '../model/dto/Category';
import {Widget} from '../model/dto/Widget';
import {ProjectWidget} from '../model/dto/ProjectWidget';

@Injectable()
export class WidgetService extends AbstractHttpService  {

  constructor(private http: HttpClient) {
    super();
  }

  getCategories(): Observable<Category[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');

    return this.http
        .get<Category[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.WIDGET_URL}/categories`, {headers: headers})
        .map(response => AbstractHttpService.extractData(response))
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }

  getWidgetsByCategoryId(categoryId: number): Observable<Widget[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');

    return this.http
        .get<Widget[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.WIDGET_URL}/category/${categoryId}`, {headers: headers})
        .map(response => AbstractHttpService.extractData(response))
        .catch((error: any) => AbstractHttpService.handleErrorObservable(error));
  }
}
