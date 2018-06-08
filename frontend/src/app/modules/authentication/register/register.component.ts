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

import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {CustomValidators} from 'ng2-validation';
import {checkPasswordMatch} from '../../../shared/validators/CustomValidator';
import {User} from '../../../shared/model/dto/user/User';
import {ICredentials} from '../../../shared/model/dto/user/ICredentials';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../../shared/auth/authentication.service';

/**
 * Component that register a new user
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  /**
   * The register form
   */
  registerForm: FormGroup;
  /**
   * The form control for the password confirmation
   */
  confirmPasswordControl: FormControl;
  /**
   * The form control for the password
   */
  passwordControl: FormControl;

  /**
   * Tell if the form has been submit or not
   *
   * @type {boolean} true if the form is submitting, false otherwise
   */
  formSubmitAttempt = false;
  /**
   * If the password should be hide or not
   * @type {boolean}
   */
  hidePassword = true;

  /**
   * Constructor
   *
   * @param {FormBuilder} formBuilder The formBuilder service to inject
   * @param {AuthenticationService} authenticationService The authentication service to inject
   * @param {Router} router The router service to inject
   */
  constructor(private formBuilder: FormBuilder,
              private authenticationService: AuthenticationService,
              private router: Router) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.authenticationService.logout();

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

  /**
   * Send the register form
   */
  signUp() {
    this.formSubmitAttempt = true;

    const user: User = this.registerForm.value;
    this
        .authenticationService
        .register(user)
        .subscribe(() => {
          const credentials: ICredentials = {username: user.username, password: user.password};
          this
              .authenticationService
              .authenticate(credentials)
              .subscribe(
                  () => {
                    // Authentication succeed
                    this.router.navigate(['/home']);
                  },
                  error => {
                    // Authentication failed
                    this.formSubmitAttempt = false;
                    console.log(error);
                  }
              );
        });
  }
}
