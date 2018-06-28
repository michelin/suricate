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
import {AuthenticationService} from '../authentication.service';

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
   * The form control for the password confirmation
   * @type {FormControl}
   * @private
   */
  private _confirmPasswordControl: FormControl;
  /**
   * The form control for the password
   * @type {FormControl}
   * @private
   */
  private _passwordControl: FormControl;

  /**
   * The register form
   * @type {FormGroup}
   */
  registerForm: FormGroup;
  /**
   * Tell if the form has been submit or not
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
   * @param {FormBuilder} _formBuilder The formBuilder service to inject
   * @param {AuthenticationService} _authenticationService The authentication service to inject
   * @param {Router} _router The router service to inject
   */
  constructor(private _formBuilder: FormBuilder,
              private _authenticationService: AuthenticationService,
              private _router: Router) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this._authenticationService.logout();

    this._passwordControl = this._formBuilder
        .control(
            '',
            [Validators.required, Validators.minLength(3)]
        );

    this._confirmPasswordControl = this._formBuilder
        .control(
            '',
            [Validators.required, Validators.minLength(3), checkPasswordMatch(this._passwordControl)]
        );

    this.registerForm = this._formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      firstname: ['', [Validators.required, Validators.minLength(2)]],
      lastname: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, CustomValidators.email]],
      password: this._passwordControl,
      confirmPassword: this._confirmPasswordControl
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
        ._authenticationService
        .register(user)
        .subscribe(() => {
          const credentials: ICredentials = {username: user.username, password: user.password};
          this
              ._authenticationService
              .authenticate(credentials)
              .subscribe(
                  () => {
                    // Authentication succeed
                    this._router.navigate(['/home']);
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
