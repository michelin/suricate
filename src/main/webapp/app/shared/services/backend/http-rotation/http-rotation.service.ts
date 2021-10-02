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

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Project } from '../../../models/backend/project/project';
import { ProjectWidget } from '../../../models/backend/project-widget/project-widget';
import { ProjectRequest } from '../../../models/backend/project/project-request';
import { ProjectWidgetPositionRequest } from '../../../models/backend/project-widget/project-widget-position-request';
import { ProjectWidgetRequest } from '../../../models/backend/project-widget/project-widget-request';
import { User } from '../../../models/backend/user/user';
import { WebsocketClient } from '../../../models/backend/websocket-client';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { Page } from '../../../models/backend/page';
import {Rotation} from "../../../models/backend/rotation/rotation";
import {RotationRequest} from "../../../models/backend/rotation/rotation-request";

@Injectable({ providedIn: 'root' })
export class HttpRotationService {
  /**
   * Global endpoint for projects
   */
  private static readonly rotationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/rotations`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get all rotations of current user
   *
   * @returns The rotations of the current user
   */
  public getAllForCurrentUser(): Observable<Rotation[]> {
    const url = `${HttpRotationService.rotationsApiEndpoint}/currentUser`;

    return this.httpClient.get<Rotation[]>(url);
  }

  /**
   * Get a rotation by token
   *
   * @param token The rotation token
   * @returns The rotation as observable
   */
  public getByToken(token: string): Observable<Rotation> {
    const url = `${HttpRotationService.rotationsApiEndpoint}/${token}`;

    return this.httpClient.get<Rotation>(url);
  }

  /**
   * Create a rotation
   *
   * @param rotation The rotation to create
   */
  public create(rotation: RotationRequest): Observable<Rotation> {
    const url = `${HttpRotationService.rotationsApiEndpoint}`;

    return this.httpClient.post<Rotation>(url, rotation);
  }

  /**
   * Delete a rotation
   *
   * @param token The rotation token
   */
  public delete(token: string): Observable<void> {
    const url = `${HttpRotationService.rotationsApiEndpoint}/${token}`;

    return this.httpClient.delete<void>(url);
  }

  /**
   * Update rotation
   *
   * @param token The token of the rotation to update
   * @param rotation The rotation to update
   */
  public update(token: string, rotation: RotationRequest): Observable<void> {
    const url = `${HttpRotationService.rotationsApiEndpoint}/${token}`;

    return this.httpClient.put<void>(url, rotation);
  }

  /**
   * Get the list of clients connected by websocket to a rotation
   *
   * @param token The rotation token
   */
  public getRotationWebsocketClients(token: string): Observable<WebsocketClient[]> {
    const url = `${HttpRotationService.rotationsApiEndpoint}/${token}/websocket/clients`;
    return this.httpClient.get<WebsocketClient[]>(url);
  }
}
