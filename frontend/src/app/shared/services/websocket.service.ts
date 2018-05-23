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
import {StompService} from 'ng2-stomp-service/index';
import {WSConfiguration} from '../model/websocket/WSConfiguration';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {empty} from 'rxjs/observable/empty';
import {fromPromise} from 'rxjs/observable/fromPromise';
import {NumberUtils} from '../utils/NumberUtils';

/**
 * Service that manage the websockets connections
 */
@Injectable()
export class WebsocketService extends AbstractHttpService {

  /** Inventory WebSocket status **/
  public static readonly WS_STATUS_CLOSED = 'CLOSED';
  public static readonly WS_STATUS_CONNECTING = 'CONNECTING';
  public static readonly WS_STATUS_CONNECTED = 'CONNECTED';

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
   *
   * @param {StompService} stompService The websocket service from ng2-STOMP-OVER-Websocket plugin
   */
  constructor(private stompService: StompService) {
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
  /*                    Dashboard specific                              */
  /* ****************************************************************** */

  /**
   * Get the dashboard configuration for websocket
   *
   * @returns {WSConfiguration} The configuration
   */
  getDashboardWSConfiguration(): WSConfiguration {
    return {
      host: `${AbstractHttpService.BASE_WS_URL}`,
      debug: true,
      queue: {'init': false}
    };
  }


  /* ****************************************************************** */
  /*                    Global Management                               */
  /* ****************************************************************** */

  /**
   * Handle the connection of a websocket
   *
   * @param {WSConfiguration} configuration
   */
  connect(configuration: WSConfiguration): Observable<any> {
    // configuration
    this.stompService.configure(configuration);

    // start connection
    return fromPromise(
        this.stompService.startConnect().then(() => {
          this.stompService.done('init');
          console.log('connected');

          return empty();
        })
    );
  }

  /**
   * Handle the subcription of a websocket
   *
   * @param {string} eventUrl The subcription url
   * @param {Function} callbackFunction The callback function to call when a new event is received
   */
  subscribe(eventUrl: string, callbackFunction: Function): Subscription {
    if (this.stompService.status !== WebsocketService.WS_STATUS_CONNECTED) {
      Observable.throw(new Error('No connection found, connect your websocket before subscribe to an event'));
    }

    return this.stompService.subscribe(`${eventUrl}`, callbackFunction);
  }

  /**
   * Unsubscribe to an event
   *
   * @param {Subscription} subscription The subscription to close
   */
  unsubscribe(subscription: Subscription) {
    subscription.unsubscribe();
  }

  /**
   * Handle the disconnection of the websocket
   */
  disconnect() {
    if (this.stompService.status === WebsocketService.WS_STATUS_CONNECTED) {
      this.stompService.disconnect().then(() => {
        console.log('Connection closed');
      });
    }
  }
}
