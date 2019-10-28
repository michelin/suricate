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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { animate, style, transition, trigger } from '@angular/animations';

import { DataTypeEnum } from '../../enums/data-type.enum';
import { FormField } from '../../models/frontend/form/form-field';
import { FormChangeEvent } from '../../models/frontend/form/form-change-event';

/**
 * Manage the instantiation of different form inputs
 */
@Component({
  selector: 'suricate-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss'],
  animations: [
    trigger('animationError', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-100%)' }),
        animate('300ms cubic-bezier(0.55, 0, 0.55, 0.2)', style({ opacity: 1, transform: 'translateY(0%)' }))
      ])
    ])
  ]
})
export class InputComponent {
  /**
   * The form created in which we have to create the input
   */
  @Input()
  formGroup: FormGroup;
  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  field: FormField;
  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  valueChangeEvent = new EventEmitter<FormChangeEvent>();
  /**
   * The data type enum
   */
  dataType = DataTypeEnum;

  /**
   * Constructor
   */
  constructor() {}

  /**
   * Retrieve the form control from the form
   */
  getFormControl(): AbstractControl | null {
    return this.formGroup.controls[this.field.key];
  }

  /**
   * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
   *
   * @param value The new value
   */
  emitValueChange(value: any): void {
    this.valueChangeEvent.emit({
      inputKey: this.field.key,
      value: value
    });
  }

  /**
   * Tell if it's a required field
   */
  isRequired(): boolean {
    let isRequired = false;

    if (this.field && this.field.validators && this.field.validators && !this.field.readOnly) {
      isRequired = Array.isArray(this.field.validators)
        ? (this.field.validators as ValidatorFn[]).includes(Validators.required)
        : this.field.validators === Validators.required;
    }

    return isRequired;
  }

  /**
   * Test if the field is on error
   */
  isInputFieldOnError(): boolean {
    return (this.getFormControl().dirty || this.getFormControl().touched) && this.getFormControl().invalid;
  }
}
