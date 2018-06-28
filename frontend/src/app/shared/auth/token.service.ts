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
import {JwtHelperService} from '@auth0/angular-jwt';

/**
 * The token service
 */
@Injectable()
export class TokenService {

  /**
   * Service used for decoding the token
   * @type {JwtHelperService}
   * @private
   */
  private _jwtHelperService = new JwtHelperService();

  /**
   * Constructor
   */
  constructor() {
  }

  /* ***************************************************** */
  /*                     Getter / Setter                   */

  /* ***************************************************** */

  /**
   * Get the token from localStorage
   *
   * @returns {string | null}
   */
  get token() {
    return localStorage.getItem('token');
  }

  /**
   * Set the token into localStorage
   *
   * @param {string} newToken The new token
   */
  set token(newToken: string) {
    localStorage.setItem('token', newToken);
  }

  /* ***************************************************** */
  /*                     Methods                           */

  /* ***************************************************** */

  /**
   * Tell if the user has a token or not
   * @returns {boolean} True if the user have a token store, false otherwise
   */
  hasToken(): boolean {
    return !!this.token;
  }

  /**
   * True if the token has expired, False otherwise
   * @returns {boolean}
   */
  isTokenExpired(): boolean {
    return this._jwtHelperService.isTokenExpired(this.token);
  }

  /**
   * Get the list of roles for the current user
   * @returns {string[]} The list of roles
   */
  getUserRoles(): string[] {
    return this._jwtHelperService.decodeToken(this.token).authorities;
  }
}
