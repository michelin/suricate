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
import {CanActivate, CanActivateChild, Router} from '@angular/router';
import {Observable, of} from 'rxjs';

import {UserService} from '../../../modules/security/user/user.service';

/**
 * The admin guard
 */
@Injectable()
export class AdminGuard implements CanActivate, CanActivateChild {

  /**
   * The constructor
   *
   * @param {UserService} userService The user service
   * @param {Router} router The router
   */
  constructor(private userService: UserService,
              private router: Router) {

  }

  /**
   * Activate root routes
   * @returns {Observable<boolean>}
   */
  canActivate(): Observable<boolean> {
    if (this.userService.isAdmin()) {
      return of(true);
    }

    this.router.navigate(['home']);
    return of(false);
  }

  /**
   * For Child routes
   * @returns {Observable<boolean>}
   */
  canActivateChild(): Observable<boolean> {
    return this.canActivate();
  }
}
