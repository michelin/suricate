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

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';

import {ProjectWidget} from '../../../../shared/model/dto/ProjectWidget';

/**
 * Dialog used for displaying "Yes / No" popup
 */
@Component({
  selector: 'app-delete-project-widget-dialog',
  templateUrl: './delete-project-widget-dialog.component.html',
  styleUrls: ['./delete-project-widget-dialog.component.css']
})
export class DeleteProjectWidgetDialogComponent implements OnInit {

  /**
   * The project widget to delete
   * @type {ProjectWidget}
   */
  projectWidget: ProjectWidget;

  /**
   * Constructor
   *
   * @param data The data give to the dialog
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {
  }

  /**
   * When the dialog is init
   */
  ngOnInit() {
    this.projectWidget = this.data.projectWidget;
  }

}
