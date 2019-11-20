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
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthenticationService } from '../services/frontend/authentication.service';
import { AbstractHttpService } from '../services/backend/abstract-http.service';

/**
 * Used to put the token in the request
 */
@Injectable({ providedIn: 'root' })
export class TokenInterceptor implements HttpInterceptor {
  /**
   * Method implemented from HttpInterceptor
   *
   * @param {HttpRequest<any>} request The request sent
   * @param {HttpHandler} next The next interceptor
   * @returns {Observable<HttpEvent<any>>}
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (
      !request ||
      !request.url ||
      (/^http/.test(request.url) && !(AbstractHttpService.baseApiEndpoint && request.url.startsWith(AbstractHttpService.baseApiEndpoint)))
    ) {
      return next.handle(request);
    }

    if (AuthenticationService.isLoggedIn()) {
      request = request.clone({
        setHeaders: {
          Authorization: AuthenticationService.getFullToken()
        }
      });
    }

    return next.handle(request);
  }
}
