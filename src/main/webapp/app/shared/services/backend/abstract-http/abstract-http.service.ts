/*
 * Copyright 2012-2021 the original author or authors.
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
import { Observable } from 'rxjs';
import { EnvironmentService } from '../../frontend/environment/environment.service';
import { Page } from '../../../models/backend/page';
import { HttpFilter } from '../../../models/backend/http-filter';

/**
 * Service used to define the minimum requirement for an http service
 */
@Injectable({ providedIn: 'root' })
export abstract class AbstractHttpService<T> {
  /**
   * The base API url
   * @type {string}
   */
  public static readonly baseApiEndpoint = `${EnvironmentService.baseEndpoint}/api`;

  /**
   * Function used to retrieve the list of Objects
   */
  abstract getAll(filter?: HttpFilter): Observable<T[] | Page<T>>;

  /**
   * Function used to retrieve an Object of type T
   *
   * @param id The object id to retrieve
   */
  abstract getById(id: number | string): Observable<T>;

  /**
   * Function used to create an object of type T
   *
   * @param entity The object that we want to create
   */
  abstract create(entity: T): Observable<T>;

  /**
   * Function used to update an object of type T
   *
   * @param id The object id if to update
   * @param entity The new object for this id
   */
  abstract update(id: number | string, entity: T): Observable<void>;

  /**
   * Function used to delete an object
   *
   * @param id The object id to delete
   */
  abstract delete(id: number | string): Observable<void>;
}
