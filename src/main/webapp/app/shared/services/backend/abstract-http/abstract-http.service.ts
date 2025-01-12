/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { EnvironmentService } from '../../frontend/environment/environment.service';

/**
 * Service used to define the minimum requirement for a http service
 */
@Injectable({ providedIn: 'root' })
export abstract class AbstractHttpService<TRet, TReq> {
  /**
   * The base API url
   */
  public static readonly baseApiEndpoint = `${EnvironmentService.backendUrl}/api`;

  /**
   * Function used to retrieve the list of Objects
   */
  abstract getAll(filter?: HttpFilter): Observable<PageModel<TRet>>;

  /**
   * Function used to retrieve an Object of type T
   *
   * @param id The object id to retrieve
   */
  abstract getById(id: number | string): Observable<TRet>;

  /**
   * Function used to create an object of type T
   *
   * @param entity The object that we want to create
   */
  abstract create(entity: TReq): Observable<TRet>;

  /**
   * Function used to update an object of type T
   *
   * @param id The object id if to update
   * @param entity The new object for this id
   */
  abstract update(id: number | string, entity: TReq): Observable<void>;

  /**
   * Function used to delete an object
   *
   * @param id The object id to delete
   */
  abstract delete(id: number | string): Observable<void>;
}
