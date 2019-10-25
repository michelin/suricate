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

/**
 * This component is used to display information (without actions to do)
 */
@Component({
  selector: 'app-communication-dialog',
  templateUrl: './communication-dialog.component.html',
  styleUrls: ['./communication-dialog.component.scss']
})
export class CommunicationDialogComponent implements OnInit {
  /**
   * The dialog title
   */
  title: String;

  /**
   * The message to display
   */
  message: String;

  /**
   * True if it's an error message, false otherwise
   */
  isErrorMessage: boolean;

  /**
   * Constructor
   *
   * @param data The data object that contains every informations to display
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {}

  /**
   * Called when the dialog is init
   */
  ngOnInit() {
    this.title = this.data.title;
    this.message = this.data.message;
    this.isErrorMessage = this.data.isErrorMessage;
  }
}
