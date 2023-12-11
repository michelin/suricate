/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

import { Component, EventEmitter, inject, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { AbstractControl, UntypedFormGroup, Validators } from '@angular/forms';
import { animate, style, transition, trigger } from '@angular/animations';

import { DataTypeEnum } from '../../../enums/data-type.enum';
import { ValueChangedEvent, ValueChangedType } from '../../../models/frontend/form/value-changed-event';
import { FormOption } from '../../../models/frontend/form/form-option';
import { FormField } from '../../../models/frontend/form/form-field';
import { IconEnum } from '../../../enums/icon.enum';
import { MaterialIconRecords } from '../../../records/material-icon.record';
import { DashboardScreenComponent } from '../../../../dashboard/components/dashboard-screen/dashboard-screen.component';
import { TranslateService } from '@ngx-translate/core';

/**
 * Manage the instantiation of different form inputs
 */
@Component({
  selector: 'suricate-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss'],
  encapsulation: ViewEncapsulation.None,
  animations: [
    trigger('animationError', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-100%)' }),
        animate('300ms cubic-bezier(0.55, 0, 0.55, 0.2)', style({ opacity: 1, transform: 'translateY(0%)' }))
      ])
    ])
  ]
})
export class InputComponent implements OnInit {
  /**
   * The form created in which we have to create the input
   */
  @Input()
  public belongingComponent: DashboardScreenComponent;

  /**
   * The form created in which we have to create the input
   */
  @Input()
  public formGroup: UntypedFormGroup;

  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  public field: FormField;

  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  public valueChangeEvent = new EventEmitter<ValueChangedEvent>();

  /**
   * Translate service
   */
  protected translateService: TranslateService;

  /**
   * The data type enum
   */
  public dataType = DataTypeEnum;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icon codes
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of options to display
   */
  public options: FormOption[];

  /**
   * Is the current field a password or not
   */
  public originalTypeIsPassword: boolean;

  /**
   * Constructor
   */
  constructor() {
    this.translateService = inject(TranslateService);
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.originalTypeIsPassword = this.field.type === DataTypeEnum.PASSWORD;

    this.initOptionsField();
  }

  /**
   * Init the field options
   */
  private initOptionsField(): void {
    if (this.field.options) {
      this.field.options().subscribe((options: FormOption[]) => {
        this.options = options;
      });
    }
  }

  /**
   * Retrieve the form control from the form
   */
  public getFormControl(): AbstractControl | null {
    return this.formGroup.controls[this.field.key];
  }

  /**
   * Get the first error triggered by the current field.
   * Return the string code of the error to display it.
   */
  public getInputErrors(): string {
    if (this.getFormControl()['errors']?.required) {
      return 'field.error.required';
    }

    if (this.getFormControl()['errors']?.minlength) {
      return 'field.error.length';
    }

    if (this.getFormControl()['errors']?.email) {
      return 'field.error.email.format';
    }

    if (this.getFormControl()['errors']?.passwordMismatch) {
      return 'field.error.password.mismatch';
    }

    if (this.getFormControl()['errors']?.pattern) {
      return 'field.error.pattern';
    }

    if (this.getFormControl()['errors']?.digits) {
      return 'field.error.digits';
    }

    if (this.getFormControl()['errors']?.gt0) {
      return 'field.error.gt0';
    }

    if (this.getFormControl()['errors']?.uniquePriority) {
      return 'field.error.repository.unique.priority';
    }
  }

  /**
   * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
   */
  public emitValueChange(type: ValueChangedType): void {
    this.manageAutoCompleteChanges();

    this.valueChangeEvent.emit({
      fieldKey: this.field.key,
      value: this.formGroup.value[this.field.key],
      type: type
    });
  }

  /**
   * Refresh the list to display in auto complete
   */
  private manageAutoCompleteChanges(): void {
    if (this.field.options && this.field.type === DataTypeEnum.TEXT) {
      const inputValue = this.formGroup.value[this.field.key];

      this.field.options(inputValue).subscribe(options => {
        this.options = options;
      });
    }
  }

  /**
   * Tell if it's a required field
   */
  public isRequired(): boolean {
    let isRequired = false;

    if (this.field && this.field.validators && this.field.validators && !this.field.readOnly) {
      isRequired = Array.isArray(this.field.validators)
        ? this.field.validators.includes(Validators.required)
        : this.field.validators === Validators.required;
    }

    return isRequired;
  }

  /**
   * Test if the field is on error
   */
  public isInputFieldOnError(): boolean {
    return (this.getFormControl().dirty || this.getFormControl().touched) && this.getFormControl().invalid;
  }

  /**
   * Execute an action when clicking on the suffix icon depending on the type of
   * the field
   */
  public suffixActions(): void {
    if (this.originalTypeIsPassword) {
      if (this.field.type === DataTypeEnum.PASSWORD) {
        this.field.type = DataTypeEnum.TEXT;
        this.field.iconSuffix = IconEnum.HIDE_PASSWORD;
      } else {
        this.field.type = DataTypeEnum.PASSWORD;
        this.field.iconSuffix = IconEnum.SHOW_PASSWORD;
      }
    }
  }
}
