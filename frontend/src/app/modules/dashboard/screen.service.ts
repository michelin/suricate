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
import {AbstractHttpService} from '../../shared/services/abstract-http.service';
import {HttpClient} from '@angular/common/http';
import {WebsocketClient} from '../../shared/model/dto/WebsocketClient';

@Injectable()
export class ScreenService extends AbstractHttpService {

  /**
   * Screen base url
   * @type {string}
   * @private
   */
  private static readonly SCREENS_BASE_URL = `${AbstractHttpService.BASE_API_URL}/${AbstractHttpService.SCREENS_URL}`;

  /**
   * The constructor
   *
   * @param {HttpClient} _httpClient The http client service
   */
  constructor(private _httpClient: HttpClient) {
    super();
  }


  /**
   * Send the notification for connect a new tv to this dashboard
   *
   * @param {string} projectToken The project token to connect
   * @param {number} screenCode The tv screen code
   */
  connectProjectToScreen(projectToken: string, screenCode: number): void {
    const url = `${ScreenService.SCREENS_BASE_URL}/connect/${screenCode}/project/${projectToken}`;
    this._httpClient.get<void>(url).subscribe();
  }

  /**
   * Send the notification to disconnect a tv for this dashboard
   *
   * @param {WebsocketClient} websocketClient The client to disconnect
   */
  disconnectScreen(websocketClient: WebsocketClient): void {
    const url = `${ScreenService.SCREENS_BASE_URL}/disconnect/`;
    this._httpClient.put<void>(url, websocketClient).subscribe();
  }

  /**
   * Refresh every screens for a project token
   *
   * @param {string} projectToken The project token to refresh
   */
  refreshEveryConnectedScreensForProject(projectToken: string): void {
    const url = `${ScreenService.SCREENS_BASE_URL}/refresh/${projectToken}`;
    this._httpClient.get<void>(url).subscribe();
  }

  /**
   * Display the screen code on every connected screens
   * @param {string} projectToken The project token
   */
  displayScreenCodeEveryConnectedScreensForProject(projectToken: string): void {
    const url = `${ScreenService.SCREENS_BASE_URL}/screencode/${projectToken}`;
    this._httpClient.get<void>(url).subscribe();
  }
}
