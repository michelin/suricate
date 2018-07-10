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

import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {tap} from 'rxjs/operators';
import {Injectable} from '@angular/core';

import {ToastService} from '../components/toast/toast.service';
import {badCredentialError} from '../../app.constant';
import {ToastType} from '../model/toastNotification/ToastType';

/**
 * Intercptor that manage http errors
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  /**
   * Constructor
   *
   * @param {ToastService} toastService The toast service
   */
  constructor(private toastService: ToastService) {
  }

  /**
   * Method that intercept the request
   *
   * @param {HttpRequest<any>} request The request
   * @param {HttpHandler} next The next handler
   * @return {Observable<HttpEvent<any>>} The http request as event
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
        .pipe(
            tap(
                (event: HttpEvent<any>) => {
                },
                (httpError: any) => {
                  if (httpError instanceof HttpErrorResponse) {
                    switch (httpError.status) {
                      case 400:
                        if (httpError.error.error === badCredentialError) {
                          this.toastService.sendMessage('Bad credentials', ToastType.DANGER, 'Wrong login or password');
                        }
                        break;

                      case 0:
                        this.displayUnknowErrorMessage();
                        break;
                    }
                  }
                })
        );
  }

  /**
   * Display the message when an unknown error occured
   */
  displayUnknowErrorMessage() {
    this.toastService.sendMessage('Server Unavailable', ToastType.DANGER, 'The server is not responsding, please contact an administrator');
  }
}
