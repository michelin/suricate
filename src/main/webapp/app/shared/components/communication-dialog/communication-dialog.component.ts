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

import { CdkScrollable } from '@angular/cdk/scrolling';
import { NgClass } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonColorEnum } from '../../enums/button-color.enum';
import { IconEnum } from '../../enums/icon.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { CommunicationDialogConfiguration } from '../../models/frontend/dialog/communication-dialog-configuration';
import { ButtonsComponent } from '../buttons/buttons.component';

/**
 * This component is used to display information (without actions to do)
 */
@Component({
  selector: 'suricate-communication-dialog',
  templateUrl: './communication-dialog.component.html',
  styleUrls: ['./communication-dialog.component.scss'],
  standalone: true,
  imports: [
    MatDialogTitle,
    CdkScrollable,
    MatDialogContent,
    NgClass,
    MatDialogActions,
    ButtonsComponent,
    MatDialogClose,
    TranslatePipe
  ]
})
export class CommunicationDialogComponent implements OnInit {
  /**
   * The configuration of the confirmation dialog
   */
  public configuration: CommunicationDialogConfiguration;

  /**
   * The buttons
   */
  public buttons: ButtonConfiguration<unknown>[] = [];

  /**
   * Constructor
   *
   * @param data The data object that contains every information to display
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: CommunicationDialogConfiguration) {
    this.initButtons();
  }

  /**
   * Called when the dialog is init
   */
  public ngOnInit(): void {
    this.configuration = this.data;
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.buttons.push({
      label: 'close',
      icon: IconEnum.CLOSE,
      color: ButtonColorEnum.WARN
    });
  }
}
