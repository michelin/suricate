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

import { Injectable } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn } from '@angular/forms';

import { FormField } from '../../models/frontend/form/form-field';
import { FormStep } from '../../models/frontend/form/form-step';
import { DataTypeEnum } from '../../enums/data-type.enum';

/**
 * Service class that manage the instantiations of forms
 */
@Injectable({ providedIn: 'root' })
export class FormService {
  /**
   * Constructor
   *
   * @param formBuilder The form builder service
   */
  constructor(private readonly formBuilder: FormBuilder) {}

  /**
   * Validate the form
   *
   * @param formGroup The form to validate
   */
  public validate(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);

      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validate(control);
      }
    });
  }

  /**
   * Generate a form group for the steps
   *
   * @param steps The form steps to instantiate
   * @return The generated form group for the list of steps give in argument
   */
  public generateFormGroupForSteps(steps: FormStep[]): FormGroup {
    const formGroup = this.formBuilder.group({});

    steps.forEach((step: FormStep) => {
      formGroup.addControl(step.key, this.generateFormGroupForFields(step.fields));
    });

    return formGroup;
  }

  /**
   * Generate a form group for the fields
   *
   * @param fields The form fields to instantiate
   * @return The generated form group for the list of fields give in argument
   */
  public generateFormGroupForFields(fields: FormField[]): FormGroup {
    const formGroup = this.formBuilder.group({});

    if (fields) {
      fields.forEach(field => {
        if (field.type === DataTypeEnum.FIELDS) {
          formGroup.addControl(field.key, this.generateFormArrayForField(field));
        } else {
          formGroup.addControl(field.key, this.generateFormControl(field));
        }
      });
    }

    return formGroup;
  }

  /**
   * Add controls to the given form group for the given fields
   *
   * @param form The form from which add the controls
   * @param fields The form fields to instantiate
   */
  public addControlsToFormGroupForFields(form: FormGroup, fields: FormField[]): void {
    if (fields) {
      fields.forEach(field => {
        if (field.type === DataTypeEnum.FIELDS) {
          form.addControl(field.key, this.generateFormArrayForField(field));
        } else {
          form.addControl(field.key, this.generateFormControl(field));
        }
      });
    }
  }

  /**
   * Remove controls from the given form group for the given fields
   *
   * @param form The form from which remove the controls
   * @param fields The form fields for which we want to remove the controls
   */
  public removeControlsToFormGroupForFields(form: FormGroup, fields: FormField[]): void {
    if (fields) {
      fields.forEach(field => {
        form.removeControl(field.key);
      });
    }
  }

  /**
   * Create a form array used for fields data type
   *
   * @param field The parent field (with type equals to dataType.fields)
   */
  private generateFormArrayForField(field: FormField): FormArray {
    const formArray = this.formBuilder.array([]);

    if (field && field.fields && field.values) {
      field.values.subscribe((values: unknown[]) => {
        values.forEach((value: unknown) => {
          const formGroup = this.formBuilder.group({});
          field.fields.forEach((innerField: FormField) => {
            formGroup.addControl(innerField.key, this.generateFormControl(innerField, value[innerField.key] ? value[innerField.key] : ''));
          });

          formArray.push(formGroup);
        });
      });
    }

    return formArray;
  }

  /**
   * Generate a form control for a specific field
   *
   * @param field The field used to create the form control
   * @param value if we want to force the value
   * @return The form control that represent the field
   */
  private generateFormControl(field: FormField, value?: string | number): FormControl {
    return this.formBuilder.control(
      { value: value ? value : field.value, disabled: field.disabled },
      field.validators,
      field.asyncValidators
    );
  }

  /**
   * Set validators for a form control
   *
   * @param formControl The form control
   * @param validators The validators to set
   */
  public setValidatorsForControl(formControl: AbstractControl, validators: ValidatorFn | ValidatorFn[] | null) {
    formControl.setValidators(validators);
  }
}
