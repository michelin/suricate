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
import { FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { AuthenticationService } from '../../../shared/services/frontend/authentication.service';
import { HttpConfigurationService } from '../../../shared/services/backend/http-configuration.service';
import { ApplicationProperties } from '../../../shared/models/backend/application-properties';
import { AuthenticationProviderEnum } from '../../../shared/enums/authentication-provider.enum';
import { FormService } from '../../../shared/services/frontend/form.service';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { SimpleFormField } from '../../../shared/models/frontend/form/simple-form-field';

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
  protected loginForm: FormGroup;
  /**
   * Fields used to describe/create the form
   * @type {SimpleFormField[]}
   * @protected
   */
  protected formFields: SimpleFormField[];
  /**
   * Used to display spinner when form has been submitted
   * @type {boolean}
   * @protected
   */
  protected formSubmitAttempt = false;
  /**
   * True if the user provider is LDAP
   * @type {boolean}
   * @protected
   */
  protected isLdapServerUserProvider: boolean;

  /**
   * Constructor
   *
   * @param {Router} router Angular service used to manage the application routes
   * @param {TranslateService} translateService NgxTranslate service used to manage the translations
   * @param {HttpConfigurationService} httpConfigurationService Suricate service used to manage http calls for configurations
   * @param {AuthenticationService} authenticationService Suricate service used to manage authentications
   * @param {FormService} formService Frontend service used manage/create forms
   */
  constructor(
    private readonly router: Router,
    private readonly translateService: TranslateService,
    private readonly httpConfigurationService: HttpConfigurationService,
    private readonly authenticationService: AuthenticationService,
    private readonly formService: FormService
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
      this.isLdapServerUserProvider = applicationProperties.value.toUpperCase() === AuthenticationProviderEnum.LDAP;
    });

    this.initLoginForm();
  }

  /**
   * Create the login form
   */
  private initLoginForm(): void {
    this.generateFormFields();
    this.loginForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form fields used for the form creation
   */
  private generateFormFields(): void {
    this.translateService.get(['username', 'password']).subscribe((translations: string) => {
      this.formFields = [
        {
          key: 'username',
          label: translations['username'],
          type: DataTypeEnum.TEXT,
          validators: [Validators.required],
          matIconPrefix: 'android'
        },
        {
          key: 'password',
          label: translations['password'],
          type: DataTypeEnum.PASSWORD,
          validators: [Validators.required],
          matIconPrefix: 'lock'
        }
      ];
    });
  }

  /**
   * Execute login action
   */
  protected login(): void {
    this.formService.validate(this.loginForm);

    if (this.loginForm.valid) {
      // Display spinner
      this.formSubmitAttempt = true;

      // Try to authenticate
      this.authenticationService.authenticate(this.loginForm.value).subscribe(
        () => {
          this.navigateToHomePage();
        },
        () => {
          // Authentication failed
          this.formSubmitAttempt = false;
        }
      );
    }
  }

  /**
   * Redirect to the home page
   */
  private navigateToHomePage(): void {
    this.router.navigate(['/home']);
  }
}
