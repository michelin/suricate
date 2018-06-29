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
import {HttpEvent, HttpHandler, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {AuthenticationService} from '../../modules/authentication/authentication.service';
import {TokenService} from '../auth/token.service';
import {baseApiEndpoint} from '../../app.constant';


/**
 * The token interceptor
 */
@Injectable()
export class TokenInterceptor {

  /**
   * Constructor
   *
   * @param {AuthenticationService} authenticationService The authentication service to inject
   * @param {TokenService} tokenService The token service to inject
   */
  constructor(private authenticationService: AuthenticationService,
              private tokenService: TokenService) {
  }

  /**
   * Method implemented from HttpInterceptor
   *
   * @param {HttpRequest<any>} request The request sent
   * @param {HttpHandler} next The next interceptor
   * @returns {Observable<HttpEvent<any>>}
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!request || !request.url ||
        (/^http/.test(request.url) &&
            !(baseApiEndpoint && request.url.startsWith(baseApiEndpoint))
        )
    ) {
      return next.handle(request);
    }

    const token = this.tokenService.token || sessionStorage.getItem('token');
    if (!!token) {
      request = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token
        }
      });
    }
    return next.handle(request);
  }
}
