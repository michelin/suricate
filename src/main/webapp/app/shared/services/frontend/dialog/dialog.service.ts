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

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { ActionsDialogConfiguration } from '../../../models/frontend/dialog/actions-dialog-configuration';
import { CommunicationDialogConfiguration } from '../../../models/frontend/dialog/communication-dialog-configuration';
import { ConfirmationDialogConfiguration } from '../../../models/frontend/dialog/confirmation-dialog-configuration';

/**
 * Service used to manage confirmation dialog
 */
@Injectable({ providedIn: 'root' })
export class DialogService {
  /**
   * Subject used to manage confirmation dialog message
   */
  private readonly confirmationDialogSubject = new Subject<ConfirmationDialogConfiguration>();

  /**
   * Subject used to manage the communication dialog message
   */
  private readonly communicationDialogSubject = new Subject<CommunicationDialogConfiguration>();

  /**
   * Subject used to manage the actions' dialog message
   */
  private readonly actionsDialogSubject = new Subject<ActionsDialogConfiguration>();

  /**
   * Used to retrieve the messages
   */
  public listenConfirmationMessages(): Observable<ConfirmationDialogConfiguration> {
    return this.confirmationDialogSubject.asObservable();
  }

  /**
   * Used to send a new confirmation message
   *
   * @param confirmationConfiguration
   */
  public confirm(confirmationConfiguration: ConfirmationDialogConfiguration): void {
    this.confirmationDialogSubject.next(confirmationConfiguration);
  }

  /**
   * Used to retrieve the communication messages
   */
  public listenCommunicationMessages(): Observable<CommunicationDialogConfiguration> {
    return this.communicationDialogSubject.asObservable();
  }

  /**
   * Used to send a new communication message
   */
  public info(communicationDialogConfiguration: CommunicationDialogConfiguration): void {
    return this.communicationDialogSubject.next(communicationDialogConfiguration);
  }

  /**
   * Used to retrieve the actions messages
   */
  public listenActionsMessages(): Observable<ActionsDialogConfiguration> {
    return this.actionsDialogSubject.asObservable();
  }

  /**
   * Used to send a new actions' message
   *
   * @param actionsConfiguration
   */
  public actions(actionsConfiguration: ActionsDialogConfiguration): void {
    this.actionsDialogSubject.next(actionsConfiguration);
  }
}
