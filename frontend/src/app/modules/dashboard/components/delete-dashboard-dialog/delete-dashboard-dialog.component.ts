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
import {Project} from '../../../../shared/model/dto/Project';
import {MAT_DIALOG_DATA} from '@angular/material';

/**
 * Dialog used for displaying "Yes / No" popup
 */
@Component({
  selector: 'app-delete-dashboard-dialog',
  templateUrl: './delete-dashboard-dialog.component.html',
  styleUrls: ['./delete-dashboard-dialog.component.css']
})
export class DeleteDashboardDialogComponent implements OnInit {

  /**
   * The project we want to delete
   */
  project: Project;

  /**
   * The constructor
   *
   * @param data The given data for the modal
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any) { }

  /**
   * Called when the dialog is init
   */
  ngOnInit() {
    this.project = this.data.project;
  }

}
