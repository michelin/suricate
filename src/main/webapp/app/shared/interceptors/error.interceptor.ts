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

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

import { ToastTypeEnum } from '../enums/toast-type.enum';
import { AuthenticationService } from '../services/frontend/authentication/authentication.service';
import { ToastService } from '../services/frontend/toast/toast.service';

/**
 * Interceptor that manage http errors
 */
@Injectable({ providedIn: 'root' })
export class ErrorInterceptor implements HttpInterceptor {
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  /**
   * Method that intercept the request
   * @param request The request
   * @param next The next handler
   * @return The http request as event
   */
  public intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      tap({
        error: (httpError: HttpErrorResponse) => {
          switch (httpError.status) {
            // Authentication error, token invalid or expired
            case 401:
              AuthenticationService.logout();
              this.router.navigate(['/login']);
              break;

            case 0:
              this.displayUnknownErrorMessage();
              break;
          }
        }
      })
    );
  }

  /**
   * Display the message when an unknown error occurred
   */
  private displayUnknownErrorMessage(): void {
    this.toastService.sendMessage('server.unavailable', ToastTypeEnum.DANGER, 'server.unavailable.explanation');
  }
}
