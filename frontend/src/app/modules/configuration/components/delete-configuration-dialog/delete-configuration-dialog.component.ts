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

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';
import {Configuration} from 'jasmine-spec-reporter/built/configuration';

@Component({
  selector: 'app-delete-configuration-dialog',
  templateUrl: './delete-configuration-dialog.component.html',
  styleUrls: ['./delete-configuration-dialog.component.css']
})

export class DeleteConfigurationDialogComponent implements OnInit {

  /**
   * The configuration to delete
   */
  configuration: Configuration;

  /**
   * The constructor
   *
   * @param data The data passed to the Dialog
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {
  }

  ngOnInit() {
    this.configuration = this.data.configuration;
  }

}
