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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { TranslatePipe } from '@ngx-translate/core';

import { FormField } from '../../../models/frontend/form/form-field';
import { ValueChangedEvent, ValueChangedType } from '../../../models/frontend/form/value-changed-event';

/**
 * Manage the instantiation of the checkbox
 */
@Component({
  selector: 'suricate-checkbox',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.scss'],
  standalone: true,
  imports: [MatCheckbox, FormsModule, ReactiveFormsModule, TranslatePipe]
})
export class CheckboxComponent {
  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  public field: FormField;

  /**
   * The form created in which we have to create the input
   */
  @Input()
  public formGroup: UntypedFormGroup;

  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  public valueChangeEvent = new EventEmitter<ValueChangedEvent>();

  /**
   * Manage the changes on checkbox
   */
  public checkboxChange(): void {
    this.valueChangeEvent.emit({
      fieldKey: this.field.key,
      value: this.formGroup.value[this.field.key],
      type: 'checkbox'
    });
  }

  /**
   * Retrieve the form control from the form
   */
  public getFormControl(): AbstractControl | null {
    return this.formGroup.controls[this.field.key];
  }
}
