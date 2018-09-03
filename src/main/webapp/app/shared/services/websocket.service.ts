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
import {StompConfig, StompRService, StompState} from '@stomp/ng2-stompjs';
import {Observable} from 'rxjs';

import {NumberUtils} from '../utils/NumberUtils';
import {baseWsEndpoint} from '../../app.constant';

import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

/**
 * Service that manage the websockets connections
 */
@Injectable()
export class WebsocketService {

  /**
   * Define the min bound for the screen code random generation
   * @type {number}
   */
  private readonly minScreenCodeBound = 100000;

  /**
   * Define the max bound for the screen code random generation
   * @type {number}
   */
  private readonly maxScreenCodeBound = 999999;

  /**
   * The constructor
   *
   * @param {StompRService} stompRService The stomp Service to inject
   */
  constructor(private stompRService: StompRService) {
  }

  /* ****************************************************************** */
  /*                    Screen code management                          */

  /* ****************************************************************** */

  /**
   * Get the screen code
   * @returns {number} The screen code
   */
  getscreenCode(): number {
    return NumberUtils.getRandomIntBetween(this.minScreenCodeBound, this.maxScreenCodeBound);
  }

  /* ****************************************************************** */
  /*                    WebSocket Management                            */

  /* ****************************************************************** */

  /**
   * Get the stomp connection state
   *
   * @returns {Observable<StompState>}
   */
  get stompConnectionState$(): Observable<StompState> {
    return this.stompRService.state.asObservable();
  }

  /**
   * Get the websocket config
   *
   * @returns {StompConfig} The config
   */
  getWebsocketConfig(): StompConfig {
    const stompConfig = new StompConfig();
    stompConfig.url = () => new SockJS(baseWsEndpoint);
    stompConfig.heartbeat_in = 0;
    stompConfig.heartbeat_out = 20000;
    stompConfig.reconnect_delay = 1000;
    stompConfig.debug = true;

    return stompConfig;
  }

  /**
   * Start the websocket connection
   */
  startConnection() {
    this.stompRService.config = this.getWebsocketConfig();
    this.stompRService.initAndConnect();
  }

  /**
   * Subcribe to a queue name
   *
   * @param {string} destination The subcription url
   */
  subscribeToDestination(destination: string): Observable<Stomp.Message> {
    return this.stompRService.subscribe(destination);
  }

  /**
   * Disconnect the client
   */
  disconnect() {
    this.stompRService.disconnect();
  }
}
