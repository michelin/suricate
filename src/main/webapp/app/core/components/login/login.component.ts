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
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { HttpWidgetConfigurationService } from '../../../shared/services/backend/http-widget-configuration/http-widget-configuration.service';
import { ApplicationProperties } from '../../../shared/models/backend/application-properties';
import { AuthenticationProviderEnum } from '../../../shared/enums/authentication-provider.enum';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { LoginFormFieldsService } from '../../../shared/services/frontend/form-fields/login-form-fields/login-form-fields.service';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { SettingsService } from '../../services/settings.service';

/**
 * Manage the login page
 */
@Component({
  selector: 'suricate-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  /**
   * The login form
   * @type {FormGroup}
   * @protected
   */
  public loginForm: FormGroup;
  /**
   * Fields used to describe/create the form
   * @type {FormField[]}
   * @protected
   */
  public formFields: FormField[];
  /**
   * The list of buttons to display in the form login
   * @type {ButtonConfiguration[]}
   * @protected
   */
  public buttonConfigurations: ButtonConfiguration<unknown>[];
  /**
   * Used to display spinner when form has been submitted
   * @type {boolean}
   * @protected
   */
  public formSubmitAttempt = false;
  /**
   * True if the user provider is LDAP
   * @type {boolean}
   * @protected
   */
  protected isLdapServerUserProvider: boolean;

  /**
   * Constructor
   *
   * @param router Angular service used to manage the application routes
   * @param httpConfigurationService Suricate service used to manage http calls for configurations
   * @param authenticationService Suricate service used to manage authentications
   * @param formService Front-End service used to manage forms
   * @param settingsService Front-End service used to manage the user's settings
   */
  constructor(
    private readonly router: Router,
    private readonly httpConfigurationService: HttpWidgetConfigurationService,
    private readonly authenticationService: AuthenticationService,
    private readonly formService: FormService,
    private readonly settingsService: SettingsService
  ) {
    this.initButtons();
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    if (AuthenticationService.isLoggedIn()) {
      this.navigateToHomePage();
      return;
    }

    this.httpConfigurationService.getAuthenticationProvider().subscribe((applicationProperties: ApplicationProperties) => {
      this.isLdapServerUserProvider = applicationProperties.value.toUpperCase() === AuthenticationProviderEnum.LDAP;
    });

    this.initLoginForm();
  }

  /**
   * Execute login action
   */
  public login(): void {
    this.formService.validate(this.loginForm);

    if (this.loginForm.valid) {
      this.formSubmitAttempt = true;

      this.authenticationService.authenticate(this.loginForm.value).subscribe(
        () => {
          this.settingsService.initUserSettings(AuthenticationService.getConnectedUser());
          this.navigateToHomePage();
        },
        () => (this.formSubmitAttempt = false) // Authentication failed
      );
    }
  }

  /**
   * Initialize the list of buttons to use in the application
   */
  private initButtons(): void {
    this.buttonConfigurations = [
      {
        color: 'primary',
        label: 'sign.in',
        type: ButtonTypeEnum.SUBMIT
      },
      {
        color: 'primary',
        label: 'sign.up',
        callback: () => this.navigateToRegisterPage(),
        hidden: () => this.isLdapServerUserProvider
      }
    ];
  }

  /**
   * Create the login form
   */
  private initLoginForm(): void {
    this.formFields = LoginFormFieldsService.generateFormFields();
    this.loginForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Redirect to the home page
   */
  private navigateToHomePage(): void {
    this.router.navigate(['/home']);
  }

  /**
   * Redirect to the register page
   */
  private navigateToRegisterPage(): void {
    this.router.navigate(['/register']);
  }
}
