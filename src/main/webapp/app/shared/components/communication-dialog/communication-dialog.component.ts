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

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommunicationDialogConfiguration } from '../../models/frontend/dialog/communication-dialog-configuration';

/**
 * This component is used to display information (without actions to do)
 */
@Component({
  selector: 'suricate-communication-dialog',
  templateUrl: './communication-dialog.component.html',
  styleUrls: ['./communication-dialog.component.scss']
})
export class CommunicationDialogComponent implements OnInit {
  /**
   * The configuration of the confirmation dialog
   * @type {CommunicationDialogConfiguration}
   * @protected
   */
  protected configuration: CommunicationDialogConfiguration;

  /**
   * Constructor
   *
   * @param data The data object that contains every information to display
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: CommunicationDialogConfiguration) {}

  /**
   * Called when the dialog is init
   */
  public ngOnInit(): void {
    this.configuration = this.data;
  }
}
