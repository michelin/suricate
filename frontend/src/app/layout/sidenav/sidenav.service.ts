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

import {Injectable} from '@angular/core';
import {Subject, Observable} from 'rxjs';

/**
 * Sidenav service
 */
@Injectable()
export class SidenavService {

  /**
   * The side nav subject
   * @type {Subject<boolean>} True open, false close
   * @private
   */
  private sidenavOpenCloseEventSubject = new Subject<boolean>();

  /**
   * Constructor
   */
  constructor() {
  }

  /**
   * Observable that hold events close/open events
   *
   * @returns {Observable<boolean>}
   */
  subscribeToSidenavOpenCloseEvent(): Observable<boolean> {
    return this.sidenavOpenCloseEventSubject.asObservable();
  }

  /**
   * Send a close event
   */
  closeSidenav(): void {
    this.sidenavOpenCloseEventSubject.next(false);
  }

  /**
   * Send an open event
   */
  openSidenav(): void {
    this.sidenavOpenCloseEventSubject.next(true);
  }
}
