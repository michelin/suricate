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

import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

import {FormField} from '../../model/app/form/FormField';
import {FormStep} from '../../model/app/form/FormStep';


/**
 * Service class that manage the instantiations of forms
 */
@Injectable({
  providedIn: 'root'
})
export class FormService {

  /**
   * Constructor
   *
   * @param formBuilder The form builder service
   */
  constructor(private formBuilder: FormBuilder) {
  }

  /* *********************************************************************************************************************************** */
  /*                                         General Form Management                                                                     */

  /* *********************************************************************************************************************************** */

  /**
   * Method used to copy a formGroup into another
   *
   * @param formSource The form group we want to copy
   * @param formTarget The form group targeted
   */
  copyFormGroupToAnotherOne(formSource: FormGroup, formTarget: FormGroup): FormGroup {
    Object.keys(formSource.controls).forEach((formControlKey: string) => {
      formTarget.addControl(formControlKey, formSource.get(formControlKey));
    });

    return formTarget;
  }

  /* *********************************************************************************************************************************** */
  /*                                         Form Step Management                                                                        */

  /* *********************************************************************************************************************************** */

  /**
   * Generate a form group for the steps
   *
   * @param steps The form steps to instantiate
   * @return The generated form group for the list of steps give in argument
   */
  generateFormGroupForSteps(steps: FormStep[]): FormGroup {
    let formGroup = this.formBuilder.group({});

    steps.forEach((step: FormStep) => {
      // Generation of the form control for the step
      const generatedStepForm = this.generateFormGroupForFields(step.fields);
      formGroup = this.copyFormGroupToAnotherOne(generatedStepForm, formGroup);
    });

    return formGroup;
  }

  /**
   * Reset a form step for a form group
   *
   * @param formGroup The form group
   * @param oldStep The step to remove
   * @param newStep The new step to add to the form group
   */
  switchFormGroupStepByAnotherOne(formGroup: FormGroup, oldStep: FormStep, newStep: FormStep): FormGroup {
    this.deleteFormControlForFields(formGroup, oldStep.fields);
    return this.copyFormGroupToAnotherOne(formGroup, this.generateFormGroupForFields(newStep.fields));
  }

  /* *********************************************************************************************************************************** */
  /*                                         Form fields Management                                                                      */

  /* *********************************************************************************************************************************** */

  /**
   * Generate a form group for the fields
   *
   * @param fields The form fields to instantiate
   * @return The generated form group for the list of fields give in argument
   */
  generateFormGroupForFields(fields: FormField[]): FormGroup {
    const formGroup = this.formBuilder.group({});

    if (fields) {
      fields.forEach(field => {
        formGroup.addControl(field.key, this.generateFormControl(field));
      });
    }

    return formGroup;
  }

  /**
   * Generate a form control for a specific field
   *
   * @param field The field used to create the form control
   * @return The form control that represent the field
   */
  generateFormControl(field: FormField): FormControl {
    return this.formBuilder.control({value: field.value, disabled: field.disabled}, field.validators, field.asyncValidators);
  }

  /**
   * Delete the form control related to the form fields
   *
   * @param formGroup The form group that contains the fields
   * @param fields The fields to delete
   */
  deleteFormControlForFields(formGroup: FormGroup, fields: FormField[]) {
    fields.forEach((field: FormField) => formGroup.removeControl(field.key));
    return formGroup;
  }
}
