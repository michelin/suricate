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

import { Injectable } from '@angular/core';

import packageJson from '../../../../../../../../package.json';
import { environment } from '../../../../../environments/environment';

/**
 * The service used to manage environment variables
 */
@Injectable({ providedIn: 'root' })
export class EnvironmentService {
	/**
	 * Backend URL
	 */
	public static readonly backendUrl = `${environment.backend_url}`;

	/**
	 * When authenticating with OAuth2, frontend URL the backend has to redirect to
	 */
	public static readonly OAUTH2_FRONTEND_REDIRECT_URL = `${environment.oauth2_frontend_redirect_url}`;

	/**
	 * The global app version
	 */
	public static readonly appVersion = packageJson.version;

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
}
