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
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';

/**
 * Manage the app theme
 */
@Injectable()
export class ThemeService {

  /**
   * Hold the current theme
   *
   * @type {BehaviorSubject<string>}
   * @private
   */
  private _currentTheme$ = new BehaviorSubject<string>('default-theme');

  constructor() {
  }

  /**
   * The current theme as observable
   * @returns {Observable<string>}
   */
  getCurrentTheme(): Observable<string> {
    return this._currentTheme$.asObservable();
  }

  /**
   * Set the new theme
   *
   * @param {string} themeName
   */
  setTheme(themeName: string) {
    this._currentTheme$.next(themeName);
  }
}
