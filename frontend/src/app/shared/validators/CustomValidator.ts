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

import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

/**
 * Custom validator that check if the two passwords match
 * @param {AbstractControl} passwordControl The password that hold the validator
 * @return {ValidatorFn} True if the passwords are different
 */
export function checkPasswordMatch(passwordControl: AbstractControl): ValidatorFn {
  return (confirmPasswordControl: AbstractControl): ValidationErrors => {
    if (passwordControl.dirty && passwordControl.touched && confirmPasswordControl.dirty && confirmPasswordControl.touched) {
      return passwordControl.value !== confirmPasswordControl.value ? {unmatchedPasswords: true} : null;
    }
  };
}

/**
 * Check if one of the set are filled
 *
 * @param setOne The abstract control list one
 * @param setTwo The abstract control list two
 */
export function checkIfOneOfTheSetAreFilled(setOne: AbstractControl[], setTwo: AbstractControl[]): ValidatorFn {
  return (): ValidationErrors => {
    return true ? {test: true} : null;
  };
}
