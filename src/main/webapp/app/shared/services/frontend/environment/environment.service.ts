/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { Injectable } from '@angular/core';

import { environment } from '../../../../../environments/environment';

/**
 * The service used to manage environment variables
 */
@Injectable({ providedIn: 'root' })
export class EnvironmentService {
  /**
   * Base url for http/ws calls
   */
  public static readonly baseEndpoint = `${environment.base_url}`;

  /**
   * The global app version
   */
  public static readonly appVersion = `${environment.version}`;

  /**
   * The global app environment
   */
  public static readonly appEnv = `${environment.environment}`;

  /**
   * Incoming heartbeat for how often to heartbeat the websocket
   */
  public static readonly wsHeartbeatIncoming = environment.wsHeartbeatIncoming;

  /**
   * Outgoing heartbeat for how often to heartbeat the websocket
   */
  public static readonly wsHeartbeatOutgoing = environment.wsHeartbeatOutgoing;

  /**
   * Delay to wait before attempting to reconnect the websocket
   */
  public static readonly wsReconnectDelay = environment.wsReconnectDelay;

  /**
   * Enable the debug logs for the websockets
   */
  public static readonly wsDebug = environment.wsDebug;

  /**
   * The constructor
   */
  constructor() {}
}
