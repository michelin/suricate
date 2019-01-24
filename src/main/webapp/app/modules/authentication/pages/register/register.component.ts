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
import {Router} from '@angular/router';
import {CustomValidators} from 'ng2-validation';
import {catchError, flatMap} from 'rxjs/operators';
import {throwError} from 'rxjs';

import {AuthenticationService} from '../../authentication.service';
import {checkPasswordMatch} from '../../../../shared/validators/CustomValidator';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ApplicationProperties} from '../../../../shared/model/api/ApplicationProperties';
import {HttpConfigurationService} from '../../../../shared/services/api/http-configuration.service';
import {Credentials} from '../../../../shared/model/api/user/Credentials';
import {ToastType} from '../../../../shared/components/toast/toast-objects/ToastType';
import {UserRequest} from '../../../../shared/model/api/user/UserRequest';
import {AuthenticationProviderEnum} from '../../../../shared/model/enums/AuthenticationProviderEnum';


/**
 * Component that register a new user
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  /**
   * The form control for the password confirmation
   * @type {FormControl}
   * @private
   */
  private confirmPasswordControl: FormControl;
  /**
   * The form control for the password
   * @type {FormControl}
   * @private
   */
  private passwordControl: FormControl;

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
   * @param {FormBuilder} formBuilder The formBuilder service to inject
   * @param {AuthenticationService} authenticationService The authentication service to inject
   * @param {Router} router The router service to inject
   * @param {ToastService} toastService The toast service to inject
   * @param {HttpConfigurationService} httpConfigurationService The configuration service to inject
   */
  constructor(private formBuilder: FormBuilder,
              private authenticationService: AuthenticationService,
              private router: Router,
              private toastService: ToastService,
              private httpConfigurationService: HttpConfigurationService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.httpConfigurationService.getAuthenticationProvider().subscribe((applicationProperties: ApplicationProperties) => {
      if (applicationProperties.value === AuthenticationProviderEnum.LDAP) {
        this.router.navigate(['/login']);
      }
    });

    this.authenticationService.logout();
    this.initRegisterForm();
  }

  /**
   * Init the register form
   */
  initRegisterForm() {
    this.passwordControl = this.formBuilder.control('', [Validators.required, Validators.minLength(3)]);
    this.confirmPasswordControl = this.formBuilder.control(
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
    if (this.registerForm.valid) {
      this.formSubmitAttempt = true;
      const userRequest: UserRequest = this.registerForm.value;

      this.authenticationService.register(userRequest).pipe(
        flatMap(() => {
          const credentials: Credentials = {username: userRequest.username, password: userRequest.password};
          return this.authenticationService.authenticate(credentials);
        }),
        catchError(error => {
          console.log(error);
          return throwError(error);
        })
      ).subscribe(
        () => {
          // Authentication succeed
          this.router.navigate(['/home']);
        },
        error => {
          console.log(error);
          this.formSubmitAttempt = false;
        }
      );

    } else {
      this.toastService.sendMessage('Some fields are not properly filled', ToastType.DANGER);
    }
  }
}
