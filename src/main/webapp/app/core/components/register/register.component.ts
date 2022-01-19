/*
 * Copyright 2012-2021 the original author or authors.
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
import { FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, flatMap } from 'rxjs/operators';
import { throwError } from 'rxjs';

import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { ApplicationProperties } from '../../../shared/models/backend/application-properties';
import { HttpCategoryParametersService } from '../../../shared/services/backend/http-category-parameters/http-category-parameters.service';
import { Credentials } from '../../../shared/models/backend/user/credentials';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { UserRequest } from '../../../shared/models/backend/user/user-request';
import { AuthenticationProviderEnum } from '../../../shared/enums/authentication-provider.enum';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { CustomValidator } from '../../../shared/validators/custom-validator';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { RegisterFormFieldsService } from '../../../shared/services/frontend/form-fields/register-form-fields/register-form-fields.service';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { HttpConfigurationService } from '../../../shared/services/backend/http-configuration/http-configuration.service';

/**
 * Component used to register a new user
 */
@Component({
  selector: 'suricate-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  /**
   * The register form
   */
  public registerForm: FormGroup;

  /**
   * The description of the form
   */
  public formFields: FormField[];

  /**
   * The list of buttons to display in the form
   */
  public buttonConfigurations: ButtonConfiguration<unknown>[];

  /**
   * Define if the spinner should be running or not
   */
  public loading = true;

  /**
   * Constructor
   *
   * @param router Angular service used to manage application routes
   * @param httpConfigurationService Suricate service used to manage the configuration of the application
   * @param authenticationService Suricate service used to manage the authentication on the application
   * @param formService Frontend service used to manage the forms creations
   * @param toastService Frontend service used to display the toast messages
   */
  constructor(
    private readonly router: Router,
    private readonly httpConfigurationService: HttpConfigurationService,
    private readonly authenticationService: AuthenticationService,
    private readonly formService: FormService,
    private readonly toastService: ToastService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    if (AuthenticationService.isLoggedIn()) {
      this.navigateToHomePage();
      return;
    }

    this.httpConfigurationService.getAuthenticationProvider().subscribe((applicationProperties: ApplicationProperties) => {
      if (applicationProperties.value === AuthenticationProviderEnum.LDAP) {
        this.router.navigate(['/login']);
      }
    });

    this.initButtons();
    this.initRegisterForm();

    this.loading = false;
  }

  /**
   * Send the register form, and authenticate the user when everything is ok
   */
  public signUp(): void {
    this.formService.validate(this.registerForm);

    if (this.registerForm.valid) {
      this.loading = true;
      const userRequest: UserRequest = this.registerForm.value;

      this.authenticationService
        .register(userRequest)
        .pipe(
          flatMap(() => {
            const credentials: Credentials = { username: userRequest.username, password: userRequest.password };
            return this.authenticationService.authenticate(credentials);
          }),
          catchError(error => {
            return throwError(error);
          })
        )
        .subscribe(
          () => this.navigateToHomePage(),
          () => (this.loading = false)
        );
    } else {
      this.toastService.sendMessage('form.error.fields', ToastTypeEnum.DANGER);
    }
  }

  /**
   * Initialize the list of buttons to use in the application
   */
  private initButtons(): void {
    this.buttonConfigurations = [
      {
        color: 'primary',
        label: 'sign.up',
        type: ButtonTypeEnum.SUBMIT
      }
    ];
  }

  /**
   * Init the register form
   */
  private initRegisterForm(): void {
    this.formFields = RegisterFormFieldsService.generateFormFields();
    this.registerForm = this.formService.generateFormGroupForFields(this.formFields);
    this.formService.setValidatorsForControl(this.registerForm.get('confirmPassword'), [
      Validators.required,
      Validators.minLength(3),
      CustomValidator.checkPasswordMatch(this.registerForm.get('password'))
    ]);
  }

  /**
   * Redirect to the home page
   */
  private navigateToHomePage(): void {
    this.router.navigate(['/home']);
  }
}
