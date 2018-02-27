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
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Category} from '../../shared/model/dto/Category';
import {Widget} from '../../shared/model/dto/Widget';

@Injectable()
export class WidgetService extends AbstractHttpService  {

  constructor(private http: HttpClient) {
    super();
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.WIDGET_URL}/categories`);
  }

  getWidgetsByCategoryId(categoryId: number): Observable<Widget[]> {
    return this.http.get<Widget[]>(`${AbstractHttpService.BASE_URL}/${AbstractHttpService.WIDGET_URL}/category/${categoryId}`);
  }
}
