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

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Screen that manage the Http calls for screens
 */
@Injectable({ providedIn: 'root' })
export class HttpScreenService {
  private static readonly screensApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/screens`;
  private readonly httpClient = inject(HttpClient);

  /**
   * Send the notification for connect a new tv to this dashboard
   *
   * @param projectToken The project token to connect
   * @param screenCode The tv screen code
   */
  public connectProjectToScreen(projectToken: string, screenCode: number): Observable<void> {
    const url = `${HttpScreenService.screensApiEndpoint}/${projectToken}/connect?screenCode=${screenCode}`;

    return this.httpClient.get<void>(url);
  }

  /**
   * Send the notification to disconnect a tv for this dashboard
   *
   * @param projectToken The project token
   * @param screenCode The screen to disconnect
   */
  public disconnectScreenFromProject(projectToken: string, screenCode: number): Observable<void> {
    const url = `${HttpScreenService.screensApiEndpoint}/${projectToken}/disconnect?screenCode=${screenCode}`;

    return this.httpClient.get<void>(url);
  }

  /**
   * Refresh every screens for a project token
   *
   * @param projectToken The project token to refresh
   */
  public refreshEveryConnectedScreensForProject(projectToken: string): Observable<void> {
    const url = `${HttpScreenService.screensApiEndpoint}/${projectToken}/refresh`;

    return this.httpClient.get<void>(url);
  }

  /**
   * Display the screen code on every connected screens for a project
   *
   * @param projectToken A project token
   */
  public displayScreenCodeEveryConnectedScreensForProject(projectToken: string): Observable<void> {
    const url = `${HttpScreenService.screensApiEndpoint}/${projectToken}/showscreencode`;

    return this.httpClient.get<void>(url);
  }
}
