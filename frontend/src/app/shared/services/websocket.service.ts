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
import {AbstractHttpService} from './abstract-http.service';
import {Observable} from 'rxjs/Observable';
import {NumberUtils} from '../utils/NumberUtils';

import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import {StompConfig, StompRService} from '@stomp/ng2-stompjs';

/**
 * Service that manage the websockets connections
 */
@Injectable()
export class WebsocketService extends AbstractHttpService {

  /**
   * Define the min bound for the screen code random generation
   *
   * @type {number} The min bound
   */
  private readonly MIN_SCREEN_CODE_BOUND = 100000;

  /**
   * Define the max bound for the screen code random generation
   *
   * @type {number} The max bound
   */
  private readonly MAX_SCREEN_CODE_BOUND = 999999;

  /**
   * The constructor of the service
   */
  constructor(private stompRService: StompRService) {
    super();
  }

  /* ****************************************************************** */
  /*                    Screen code management                          */
  /* ****************************************************************** */

  /**
   * Get the screen code
   * @returns {number} The screen code
   */
  getscreenCode(): number {
    return NumberUtils.getRandomIntBetween(this.MIN_SCREEN_CODE_BOUND, this.MAX_SCREEN_CODE_BOUND);
  }

  /* ****************************************************************** */
  /*                    WebSocket Management                            */
  /* ****************************************************************** */

  /**
   * Get the websocket config
   *
   * @returns {StompConfig} The config
   */
  getWebsocketConfig(): StompConfig {
    const stompConfig = new StompConfig();
    stompConfig.url = () => new SockJS(AbstractHttpService.BASE_WS_URL);;
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

  disconnect() {
    this.stompRService.disconnect();
  }
}
