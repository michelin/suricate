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

import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {CustomValidators} from 'ng2-validation';
import {checkPasswordMatch} from '../../../shared/validators/CustomValidator';
import {AuthenticationService} from '../authentication.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  confirmPasswordControl: FormControl;
  passwordControl: FormControl;

  formSubmitAttempt = false;
  hidePassword = true;

  constructor(private formBuilder: FormBuilder,
              private authenticationService: AuthenticationService) { }

  ngOnInit() {
    this.passwordControl = this.formBuilder
        .control(
            '',
            [Validators.required, Validators.minLength(3)]
        );

    this.confirmPasswordControl = this.formBuilder
        .control(
            '',
            [Validators.required, Validators.minLength(3), checkPasswordMatch(this.passwordControl)]
        );

    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      firstname: ['', [Validators.required, Validators.minLength(2)]],
      lastname: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, CustomValidators.email]],
      password: this.passwordControl,
      confirmPassword: this.confirmPasswordControl
    });
  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.registerForm.invalid && (this.registerForm.get(field).dirty || this.registerForm.get(field).touched);
  }

  signUp() {
    this.authenticationService.register(this.registerForm.value);
  }
}
