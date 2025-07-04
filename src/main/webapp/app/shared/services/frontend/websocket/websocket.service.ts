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

import { inject, Injectable } from '@angular/core';
import { IMessage, RxStompConfig } from '@stomp/rx-stomp';
import { Observable } from 'rxjs';
import SockJS from 'sockjs-client/dist/sockjs';

import { EnvironmentService } from '../environment/environment.service';
import { RxStompService } from '../rx-stomp/rx-stomp.service';

/**
 * Service that manage the web sockets connections
 */
@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private readonly rxStompService = inject(RxStompService);

  /**
   * The base WS url
   */
  private static readonly baseWsEndpoint = `${EnvironmentService.backendUrl}/ws`;

  /**
   * Get the websocket config
   *
   * @returns The config
   */
  private getWebsocketConfig(): RxStompConfig {
    const configuration = new RxStompConfig();
    configuration.webSocketFactory = () => new SockJS(WebsocketService.baseWsEndpoint);
    configuration.brokerURL = WebsocketService.baseWsEndpoint;
    configuration.heartbeatIncoming = EnvironmentService.wsHeartbeatIncoming;
    configuration.heartbeatOutgoing = EnvironmentService.wsHeartbeatOutgoing;
    configuration.reconnectDelay = EnvironmentService.wsReconnectDelay;
    if (EnvironmentService.wsDebug) {
      configuration.debug = (str: string) => console.log(new Date(), str);
    }

    return configuration;
  }

  /**
   * Start the websocket connection
   */
  public startConnection(): void {
    this.rxStompService.configure(this.getWebsocketConfig());
    this.rxStompService.activate();
  }

  /**
   * Subscribe to a queue name
   *
   * @param {string} destination The subscription url
   */
  public watch(destination: string): Observable<IMessage> {
    return this.rxStompService.watch(destination);
  }

  /**
   * Disconnect the client
   */
  public disconnect() {
    this.rxStompService.deactivate().then();
  }
}
