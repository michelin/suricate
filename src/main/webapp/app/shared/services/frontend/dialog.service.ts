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
import { ConfirmationConfiguration } from '../../models/frontend/confirmation/confirmation-configuration';

/**
 * Service used to manage confirmation dialog
 */
@Injectable({ providedIn: 'root' })
export class DialogService {
  /**
   * Subject used to manage confirmation messages
   */
  private confirmationSubject = new Subject<ConfirmationConfiguration>();

  /**
   * Constructor
   */
  constructor() {}

  /**
   * Used to retrieve the messages
   */
  public getConfirmationMessages(): Observable<ConfirmationConfiguration> {
    return this.confirmationSubject.asObservable();
  }

  /**
   * Used to send a new confirmation message
   *
   * @param confirmationConfiguration
   */
  public confirm(confirmationConfiguration: ConfirmationConfiguration): void {
    this.confirmationSubject.next(confirmationConfiguration);
  }
}
