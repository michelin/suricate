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
import { StompConfig, StompRService } from '@stomp/ng2-stompjs';
import { Observable } from 'rxjs';
import { EnvironmentService } from './environment.service';

import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

/**
 * Service that manage the websockets connections
 */
@Injectable({ providedIn: 'root' })
export class WebsocketService {
  /**
   * The base WS url
   * @type {string}
   */
  private static readonly baseWsEndpoint = `${EnvironmentService.baseEndpoint}/ws`;

  /**
   * The constructor
   *
   * @param {StompRService} stompRService The stomp Service to inject
   */
  constructor(private readonly stompRService: StompRService) {}

  /* ****************************************************************** */
  /*                    WebSocket Management                            */

  /* ****************************************************************** */

  /**
   * Get the websocket config
   *
   * @returns {StompConfig} The config
   */
  private getWebsocketConfig(): StompConfig {
    const stompConfig = new StompConfig();
    stompConfig.url = () => new SockJS(WebsocketService.baseWsEndpoint);
    stompConfig.heartbeat_in = 0;
    stompConfig.heartbeat_out = 20000;
    stompConfig.reconnect_delay = 1000;
    stompConfig.debug = true;

    return stompConfig;
  }

  /**
   * Start the websocket connection
   */
  public startConnection(): void {
    this.stompRService.config = this.getWebsocketConfig();
    this.stompRService.initAndConnect();
  }

  /**
   * Subcribe to a queue name
   *
   * @param {string} destination The subcription url
   */
  public subscribeToDestination(destination: string): Observable<Stomp.Message> {
    return this.stompRService.subscribe(destination);
  }

  /**
   * Disconnect the client
   */
  public disconnect() {
    this.stompRService.disconnect();
  }
}
