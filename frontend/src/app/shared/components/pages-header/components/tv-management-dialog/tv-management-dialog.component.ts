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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DashboardService} from '../../../../../modules/dashboard/dashboard.service';
import {CustomValidators} from 'ng2-validation';

/**
 * Component that manage the popup for Dashboard TV Management
 *
 */
@Component({
  selector: 'app-tv-management-dialog',
  templateUrl: './tv-management-dialog.component.html',
  styleUrls: ['./tv-management-dialog.component.css']
})
export class TvManagementDialogComponent implements OnInit {

  /**
   * The register screen form
   */
  screenRegisterForm: FormGroup;

  /**
   * The project id
   */
  projectId: number;

  /**
   * Constructor
   *
   * @param data The data sent by the parent component
   * @param {FormBuilder} formBuilder The formBuilder
   * @param {DashboardService} dashboardService The dashboard service to inject
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private formBuilder: FormBuilder,
              private dashboardService: DashboardService) { }

  /**
   * When the component is initialized
   */
  ngOnInit() {
    this.projectId = this.data.projectId;
    this.screenRegisterForm = this.formBuilder.group({
      screenCode: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6), CustomValidators.digits]]
    });
  }


  /**
   * Check if the field is valid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field is valid, false otherwise
   */
  isFieldInvalid(field: string) {
    return this.screenRegisterForm.invalid && (this.screenRegisterForm.get(field).dirty || this.screenRegisterForm.get(field).touched);
  }

  /**
   * Register the screen
   */
  registerScreen() {
    if (this.screenRegisterForm.valid) {
      const screenCode: string = this.screenRegisterForm.get('screenCode').value;
      this.dashboardService.connectProjectToScreen(this.projectId, +screenCode);
    }
  }

}
