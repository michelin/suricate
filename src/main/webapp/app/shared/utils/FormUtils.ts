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

import {FormGroup} from '@angular/forms';

/**
 * Class that manage the utility function for forms management
 */
export class FormUtils {

  /**
   * Reset validators and errors for a field
   *
   * @param formGroup The form group
   * @param fieldName The field to reset
   */
  static resetValidatorsAndErrorsForField(formGroup: FormGroup, fieldName: string) {
    formGroup.get(fieldName).validator = null;
    formGroup.get(fieldName).setErrors(null);
  }

  /**
   * Check if the field is invalid or not
   *
   * @param formGroup The form
   * @param fieldName The field name
   * @returns {boolean} True if the field is invalid, false otherwise
   */
  static isFieldInvalid(formGroup: FormGroup, fieldName: string): boolean {
    return formGroup.invalid && (formGroup.get(fieldName).dirty || formGroup.get(fieldName).touched);
  }
}
