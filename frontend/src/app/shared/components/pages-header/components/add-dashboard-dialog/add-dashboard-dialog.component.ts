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

import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-add-dashboard-dialog',
  templateUrl: './add-dashboard-dialog.component.html',
  styleUrls: ['./add-dashboard-dialog.component.css']
})
export class AddDashboardDialogComponent implements OnInit {

  dashboardForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.dashboardForm = this.formBuilder.group({
      'dashboard_name': ['', [Validators.required]],
      'widget_height': ['360', [Validators.required]],
      'nb_column': ['5', [Validators.required]]
    });
  }

  /**
   * Check if the field is valid
   *
   * @param {string} field
   * @returns {boolean}
   */
  isFieldInvalid(field: string) {
    return this.dashboardForm.invalid && (this.dashboardForm.get(field).dirty || this.dashboardForm.get(field).touched);
  }

}
