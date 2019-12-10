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

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { FormSidenavConfiguration } from '../../models/frontend/sidenav/form-sidenav-configuration';

/**
 * Sidenav service
 */
@Injectable({ providedIn: 'root' })
export class SidenavService {
  /**
   * Subject used to manage form sidenav messages
   */
  private formSidenavSubject = new Subject<FormSidenavConfiguration>();

  /**
   * Constructor
   */
  constructor() {}

  /**
   * Function used to listen for new form sidenav request
   */
  public listenFormSidenavMessages(): Observable<FormSidenavConfiguration> {
    return this.formSidenavSubject.asObservable();
  }

  /**
   * Function used to open the form sidenav
   *
   * @param formSidenavConfiguration The form sidenav configuration
   */
  public openFormSidenav(formSidenavConfiguration: FormSidenavConfiguration): void {
    this.formSidenavSubject.next(formSidenavConfiguration);
  }
}
