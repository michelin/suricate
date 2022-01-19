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

import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class CustomValidator {
  private static fileFormat = '^data:image\\/(gif|jpe?g|png);base64,.+$';

  /**
   * Custom validator that checks if the two passwords match
   *
   * @param {AbstractControl} passwordControl The password that hold the validator
   * @return {ValidatorFn} True if the passwords are different
   */
  public static checkPasswordMatch(passwordControl: AbstractControl): ValidatorFn {
    return (confirmPasswordControl: AbstractControl): ValidationErrors => {
      if ((passwordControl.dirty || passwordControl.touched) && (confirmPasswordControl.dirty || confirmPasswordControl.touched)) {
        return passwordControl.value !== confirmPasswordControl.value ? { passwordMismatch: true } : null;
      }
    };
  }

  /**
   * Custom validator that checks if an input data is a digit
   *
   * @param control The field control
   */
  public static isDigits(control: AbstractControl) {
    if (control.value) {
      return String(control.value).match(new RegExp('^-?[0-9]\\d*(\\.\\d+)?$')) ? null : { digits: true };
    }
  }

  /**
   * Custom validator that checks if an input data is gt than 0
   *
   * @param control The field control
   */
  public static greaterThan0(control: AbstractControl) {
    return control.value > 0 ? null : { gt0: true };
  }

  /**
   * Custom validator that checks if an input data is gt than 0
   *
   * @param control The field control
   */
  public static greaterThanGivenValue(control: AbstractControl) {
    return control.value > 0 ? null : { gt0: true };
  }

  /**
   * Custom validator that checks if an input file has the expected format
   */
  public static fileHasFormat(): ValidatorFn {
    return Validators.pattern(this.fileFormat);
  }
}
