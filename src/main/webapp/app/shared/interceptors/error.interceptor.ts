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

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';

import { ToastService } from '../services/frontend/toast/toast.service';
import { ToastTypeEnum } from '../enums/toast-type.enum';

/**
 * Interceptor that manage http errors
 */
@Injectable({ providedIn: 'root' })
export class ErrorInterceptor implements HttpInterceptor {
  /**
   * Invalid grant error returned by spring oauth2
   * @type {string}
   */
  private static readonly badCredentialError = 'invalid_grant';

  /**
   * Constructor
   *
   * @param {ToastService} toastService The toast service
   */
  constructor(private readonly toastService: ToastService) {}

  /**
   * Method that intercept the request
   *
   * @param {HttpRequest>} request The request
   * @param {HttpHandler} next The next handler
   * @return {Observable<HttpEvent>} The http request as event
   */
  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      tap(
        (event: HttpEvent<any>) => {},
        (httpError: any) => {
          if (httpError instanceof HttpErrorResponse) {
            switch (httpError.status) {
              case 400:
                if (httpError.error.error === ErrorInterceptor.badCredentialError) {
                  this.toastService.sendMessage('credentials.error', ToastTypeEnum.DANGER);
                }
                break;

              case 0:
                this.displayUnknowErrorMessage();
                break;
            }
          }
        }
      )
    );
  }

  /**
   * Display the message when an unknown error occured
   */
  private displayUnknowErrorMessage(): void {
    this.toastService.sendMessage('server.unavailable', ToastTypeEnum.DANGER, 'server.unavailable.explanation');
  }
}
