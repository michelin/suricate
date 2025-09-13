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

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AuthenticationProvider } from '../../../enums/authentication-provider';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpConfigurationService {
	private static readonly configurationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/configurations`;
	private readonly httpClient = inject(HttpClient);

	/**
	 * Get the authentication providers authorized by the Back-End
	 */
	public getAuthenticationProviders(): Observable<AuthenticationProvider[]> {
		const url = `${HttpConfigurationService.configurationsApiEndpoint}/authentication-providers`;

		return this.httpClient.get<AuthenticationProvider[]>(url);
	}
}
