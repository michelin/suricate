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

import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { ToastType } from '../enums/toast-type';
import { AuthenticationService } from '../services/frontend/authentication/authentication-service';
import { ToastService } from '../services/frontend/toast/toast-service';

/**
 * Interceptor that manage http errors
 */
export const errorInterceptor: HttpInterceptorFn = (
	request: HttpRequest<unknown>,
	next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
	const router = inject(Router);
	const toastService = inject(ToastService);

	const displayUnknownErrorMessage = () => {
		toastService.sendMessage('server.unavailable', ToastType.DANGER, 'server.unavailable.explanation');
	};

	return next(request).pipe(
		tap({
			error: (httpError: HttpErrorResponse) => {
				switch (httpError.status) {
					// Authentication error, token invalid or expired
					case 401:
						AuthenticationService.logout();
						router.navigate(['/login']);
						break;
					case 0:
						displayUnknownErrorMessage();
						break;
				}
			}
		})
	);
};
