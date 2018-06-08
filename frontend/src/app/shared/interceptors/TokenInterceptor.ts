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

import {Injectable, Injector} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Router} from '@angular/router';
import 'rxjs/add/operator/do';

import {AuthenticationService} from '../auth/authentication.service';


/**
 * The token interceptor
 */
@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private injector: Injector,
              private router: Router,
              private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.authenticationService.token && this.authenticationService.token !== '') {
      request = request.clone({
        setHeaders: {Authorization: `Bearer ${this.authenticationService.token}`}
      });
    }

    return next.handle(request)
        .do(
            (event: HttpEvent<any>) => {
            },
            (error: any) => {
              if (error instanceof HttpErrorResponse) {
                if (error.status === 401) {
                  this.router.navigate(['/login']);
                }
              }
            }
        );
  }
}
