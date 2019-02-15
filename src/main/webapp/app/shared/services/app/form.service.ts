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
import {FormField} from '../../model/generic-component/FormField';

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

  /**
   * Generate a form group for the fields
   *
   * @param fields The form fields to instantiate
   * @return The generated form group for the list of fields give in argument
   */
  generateFormGroupForFields(fields: FormField[]): FormGroup {
    const formGroup = this.formBuilder.group({});

    fields.forEach(field => {
      formGroup.addControl(field.key, this.generateFormControl(field));
    });

    return formGroup;
  }

  /**
   * Generate a form control for a specific field
   *
   * @param field The field used to create the form control
   * @return The form control that represent the field
   */
  generateFormControl(field: FormField): FormControl {
    return this.formBuilder.control(field.value, field.validators, field.asyncValidators);
  }
}
