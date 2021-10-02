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
import { Observable } from 'rxjs';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Screen that manage the Http calls for screens
 */
@Injectable({ providedIn: 'root' })
export class HttpScreenService {
  /**
   * Global endpoint for screens
   */
  private static readonly screensApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/screens`;

  /**
   * Global endpoint for screens linked with rotation
   */
  private static readonly screensRotationApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/screens/rotation`;

  /**
   * The constructor
   *
   * @param httpClient The http client service
   */
  constructor(private readonly httpClient: HttpClient) {}

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
  public disconnectScreen(projectToken: string, screenCode: number): Observable<void> {
    const url = `${HttpScreenService.screensApiEndpoint}/${projectToken}/disconnect?screenCode=${screenCode}`;

    return this.httpClient.get<void>(url);
  }

  /**
   * Refresh every screens for a project token
   *
   * @param {string} projectToken The project token to refresh
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

  /**
   * Send the notification for connect a new tv to this rotation
   *
   * @param rotationToken The rotation token to connect
   * @param screenCode The tv screen code
   */
  public connectRotationToScreen(rotationToken: string, screenCode: number): Observable<void> {
    const url = `${HttpScreenService.screensRotationApiEndpoint}/${rotationToken}/connect?screenCode=${screenCode}`;

    return this.httpClient.get<void>(url);
  }

  /**
   * Display the screen code on every connected screens for a rotation
   *
   * @param rotationToken A dashboard or rotation token
   */
  public displayScreenCodeEveryConnectedScreensForRotation(rotationToken: string): Observable<void> {
    const url = `${HttpScreenService.screensRotationApiEndpoint}/${rotationToken}/showscreencode`;

    return this.httpClient.get<void>(url);
  }
}
