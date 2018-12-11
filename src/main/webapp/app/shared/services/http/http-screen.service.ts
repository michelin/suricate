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

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {WebsocketClient} from '../../model/api/WebsocketClient';
import {screensApiEndpoint} from '../../../app.constant';

/**
 * Screen that manage the Http calls for screens
 */
@Injectable()
export class HttpScreenService {

  /**
   * The constructor
   *
   * @param {HttpClient} httpClient The http client service
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Send the notification for connect a new tv to this dashboard
   *
   * @param {string} projectToken The project token to connect
   * @param {number} screenCode The tv screen code
   */
  connectProjectToScreen(projectToken: string, screenCode: number): void {
    const url = `${screensApiEndpoint}/connect/${screenCode}/project/${projectToken}`;

    this.httpClient.get<void>(url).subscribe();
  }

  /**
   * Send the notification to disconnect a tv for this dashboard
   *
   * @param {WebsocketClient} websocketClient The client to disconnect
   */
  disconnectScreen(websocketClient: WebsocketClient): void {
    const url = `${screensApiEndpoint}/disconnect/`;

    this.httpClient.put<void>(url, websocketClient).subscribe();
  }

  /**
   * Refresh every screens for a project token
   *
   * @param {string} projectToken The project token to refresh
   */
  refreshEveryConnectedScreensForProject(projectToken: string): void {
    const url = `${screensApiEndpoint}/refresh/${projectToken}`;

    this.httpClient.get<void>(url).subscribe();
  }

  /**
   * Display the screen code on every connected screens
   * @param {string} projectToken The project token
   */
  displayScreenCodeEveryConnectedScreensForProject(projectToken: string): void {
    const url = `${screensApiEndpoint}/screencode/${projectToken}`;

    this.httpClient.get<void>(url).subscribe();
  }
}
