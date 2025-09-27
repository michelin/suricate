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

import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

import { AbstractHttpService } from '../services/backend/abstract-http/abstract-http-service';
import { AuthenticationService } from '../services/frontend/authentication/authentication-service';

/**
 * Used to put the token in the request
 */
export const tokenInterceptor: HttpInterceptorFn = (
	request: HttpRequest<unknown>,
	next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
	const router = inject(Router);

	if (
		!request?.url ||
		(request.url.startsWith('http') &&
			!(AbstractHttpService.baseApiEndpoint && request.url.startsWith(AbstractHttpService.baseApiEndpoint)))
	) {
		return next(request);
	}

	try {
		if (AuthenticationService.isLoggedIn()) {
			request = request.clone({
				setHeaders: {
					Authorization: `Bearer ${AuthenticationService.getAccessToken()}`
				}
			});
		}
	} catch (ignoredError) {
		// Catch "isLoggedIn" errors. Token probably has been tampered
		AuthenticationService.logout();
		router.navigate(['/login']);
	}

	return next(request);
};
