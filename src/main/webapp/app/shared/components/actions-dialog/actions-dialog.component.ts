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

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { IconEnum } from '../../enums/icon.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';

@Component({
  templateUrl: './actions-dialog.component.html',
  styleUrls: ['./actions-dialog.component.scss']
})
export class ActionsDialogComponent implements OnInit {
  /**
   * The configuration of the confirmation dialog
   */
  public configuration: ActionsDialogConfiguration;

  /**
   * Constructor
   *
   * @param data The data given to the dialog
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: ActionsDialogConfiguration) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.configuration = this.data;
    this.initCloseButtonConfiguration();
  }

  /**
   * Init the buttons configurations
   */
  private initCloseButtonConfiguration(): void {
    const closeButton: ButtonConfiguration<void> = {
      label: 'close',
      icon: IconEnum.CLOSE,
      color: 'primary'
    };

    this.configuration.actions = [closeButton].concat(this.configuration.actions);
  }
}
