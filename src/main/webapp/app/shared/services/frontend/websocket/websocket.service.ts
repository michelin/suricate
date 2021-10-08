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
import { InjectableRxStompConfig, RxStompService } from '@stomp/ng2-stompjs';
import { Observable } from 'rxjs';
import { EnvironmentService } from '../environment/environment.service';

import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

/**
 * Service that manage the web sockets connections
 */
@Injectable({ providedIn: 'root' })
export class WebsocketService {
  /**
   * The base WS url
   */
  private static readonly baseWsEndpoint = `${EnvironmentService.baseEndpoint}/ws`;

  /**
   * The constructor
   *
   * @param rxStompService The stomp service for websockets
   */
  constructor(private readonly rxStompService: RxStompService) {}

  /* ****************************************************************** */
  /*                    WebSocket Management                            */
  /* ****************************************************************** */

  /**
   * Get the websocket config
   *
   * @returns The config
   */
  private getWebsocketConfig(): InjectableRxStompConfig {
    const configuration = new InjectableRxStompConfig();
    configuration.webSocketFactory = () => new SockJS(WebsocketService.baseWsEndpoint);
    configuration.heartbeatIncoming = EnvironmentService.wsHeartbeatIncoming;
    configuration.heartbeatOutgoing = EnvironmentService.wsHeartbeatOutgoing;
    configuration.reconnectDelay = EnvironmentService.wsReconnectDelay;
    configuration.debug = EnvironmentService.wsDebug ? (str: string) => console.log(new Date(), str) : () => {};

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
  public watch(destination: string): Observable<Stomp.Message> {
    return this.rxStompService.watch(destination);
  }

  /**
   * Disconnect the client
   */
  public disconnect() {
    this.rxStompService.deactivate();
  }
}
