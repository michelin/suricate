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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomValidators } from 'ng2-validation';
import { catchError, flatMap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

import { AuthenticationService } from '../../authentication.service';
import { ToastService } from '../../../../shared/services/frontend/toast.service';
import { ApplicationProperties } from '../../../../shared/models/backend/application-properties';
import { HttpConfigurationService } from '../../../../shared/services/backend/http-configuration.service';
import { Credentials } from '../../../../shared/models/backend/user/credentials';
import { ToastTypeEnum } from '../../../../shared/enums/toast-type.enum';
import { UserRequest } from '../../../../shared/models/backend/user/user-request';
import { AuthenticationProviderEnum } from '../../../../shared/enums/authentication-provider.enum';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { DataTypeEnum } from '../../../../shared/enums/data-type.enum';
import { FormService } from '../../../../shared/services/frontend/form.service';
import { CustomValidator } from '../../../../shared/validators/custom-validator';
import { SidenavService } from '../../../../layout/sidenav/sidenav.service';

/**
 * Component that register a new user
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {
  /**
   * The register form
   * @type {FormGroup}
   */
  registerForm: FormGroup;
  /**
   * The description of the form
   */
  formFields: FormField[];
  /**
   * Tell if the form has been submit or not
   * @type {boolean} true if the form is submitting, false otherwise
   */
  formSubmitAttempt = false;

  /**
   * Constructor
   *
   * @param {AuthenticationService} authenticationService The authentication service to inject
   * @param {Router} router The router service to inject
   * @param {ToastService} toastService The toast service to inject
   * @param {TranslateService} translateService The service used for translations
   * @param {FormService} formService The form service used for the form creation
   * @param {SidenavService} sidenavService Manage the sidenav
   * @param {HttpConfigurationService} httpConfigurationService The configuration service to inject
   */
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
    private toastService: ToastService,
    private translateService: TranslateService,
    private formService: FormService,
    private sidenavService: SidenavService,
    private httpConfigurationService: HttpConfigurationService
  ) {}

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.sidenavService.closeSidenav();

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
    this.generateFormFields();
    this.registerForm = this.formService.generateFormGroupForFields(this.formFields);
    this.formService.setValidatorsForControl(this.registerForm.get('confirmPassword'), [
      Validators.required,
      Validators.minLength(3),
      CustomValidator.checkPasswordMatch(this.registerForm.get('password'))
    ]);
  }

  /**
   * Generate the form fields used for the form creation
   */
  generateFormFields() {
    this.translateService
      .get(['username', 'firstname', 'lastname', 'email', 'password', 'password.confirm'])
      .subscribe((translations: string) => {
        this.formFields = [
          {
            key: 'username',
            label: translations['username'],
            type: DataTypeEnum.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'android'
          },
          {
            key: 'firstname',
            label: translations['firstname'],
            type: DataTypeEnum.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'lastname',
            label: translations['lastname'],
            type: DataTypeEnum.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'email',
            label: translations['email'],
            type: DataTypeEnum.TEXT,
            value: '',
            validators: [Validators.required, CustomValidators.email],
            matIconPrefix: 'email'
          },
          {
            key: 'password',
            label: translations['password'],
            type: DataTypeEnum.PASSWORD,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'lock'
          },
          {
            key: 'confirmPassword',
            label: translations['password.confirm'],
            type: DataTypeEnum.PASSWORD,
            value: '',
            matIconPrefix: 'lock'
          }
        ];
      });
  }

  /**
   * Send the register form
   */
  signUp() {
    this.formService.validate(this.registerForm);

    if (this.registerForm.valid) {
      this.formSubmitAttempt = true;
      const userRequest: UserRequest = this.registerForm.value;

      this.authenticationService
        .register(userRequest)
        .pipe(
          flatMap(() => {
            const credentials: Credentials = { username: userRequest.username, password: userRequest.password };
            return this.authenticationService.authenticate(credentials);
          }),
          catchError(error => {
            console.log(error);
            return throwError(error);
          })
        )
        .subscribe(
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
      this.toastService.sendMessage('Some fields are not properly filled', ToastTypeEnum.DANGER);
    }
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this.sidenavService.openSidenav();
  }
}
