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
import {WSConfiguration} from '../model/websocket/WSConfiguration';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {NumberUtils} from '../utils/NumberUtils';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {WSStatusEnum} from '../model/websocket/enums/WSStatusEnum';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

/**
 * Service that manage the websockets connections
 */
@Injectable()
export class WebsocketService extends AbstractHttpService {

  /**
   * Current websocket status
   *
   * @type {BehaviorSubject<WSStatusEnum>}
   */
  private _status = new BehaviorSubject<WSStatusEnum>(WSStatusEnum.DISCONNECTED);

  /**
   * The websocket configuration
   */
  private _configuration: WSConfiguration;

  /**
   * The SOCK JS Socket
   */
  private _socket: any;

  /**
   * The stomp client
   */
  private _stompClient: any;

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
  constructor() {
    super();
  }

  /* ****************************************************************** */
  /*                    Getter /etter                                   */
  /* ****************************************************************** */

  /**
   * The status as observable
   * @returns {Observable<WSStatusEnum>}
   */
  get stompClientStatus(): Observable<WSStatusEnum> {
    return this._status.asObservable();
  }

  /**
   * The status value
   * @returns {Observable<WSStatusEnum>}
   */
  get stompClientStatusValue(): WSStatusEnum {
    return this._status.getValue();
  }

  /**
   * Set a new configuration
   *
   * @param {WSConfiguration} configuration
   */
  set configuration(configuration: WSConfiguration) {
    this._configuration = configuration;
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
   * Get the dashboard configuration for websocket
   *
   * @returns {WSConfiguration} The configuration
   */
  getDashboardWSConfiguration(): WSConfiguration {
    return {
      host: `${AbstractHttpService.BASE_WS_URL}`,
      debug: true
    };
  }

  /**
   * Handle the connection of a websocket
   */
  startConnection() {
    if (!this._configuration) {
      Observable.throw('Configuration is required');
    }

    this._status.next(WSStatusEnum.CONNECTING);

    // Prepare connection
    this._socket = new SockJS(this._configuration.host);
    this._stompClient = Stomp.over(this._socket);

    if (this._configuration.debug) {
      this._stompClient.debug = function(message) {
        console.log(message);
      };
    }

    // Connect to server (Attributes : headers, onConnect, onError)
    this._stompClient.connect({}, this.onConnect.bind(this), this.onError.bind(this));
  }

  /**
   * Successfull connection to server
   */
  onConnect(frame: any) {
    this._status.next(WSStatusEnum.CONNECTED);
  }

  /**
   * An error as been detected
   * @param {string} error The error message
   */
  onError(error: string) {
    console.error(`Error: ${error}`);
    if (error.indexOf('Lost connection') !== -1) {
      this._status.next(WSStatusEnum.ERROR);
    }
  }

  /**
   * Handle the subcription of a websocket
   *
   * @param {string} destination The subcription url
   * @param {Function} callbackFunction The callback function to call when a new event is received
   */
  subscribeToDestination(destination: string, callbackFunction: Function): any {
    return this._stompClient.subscribe(destination, function (response) {
      const message: string = JSON.parse(response.body);
      callbackFunction(message);
    });
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
    if (this._stompClient) {
      this._stompClient.disconnect(() => {
        this._status.next(WSStatusEnum.DISCONNECTED);
      });
    }
  }
}
