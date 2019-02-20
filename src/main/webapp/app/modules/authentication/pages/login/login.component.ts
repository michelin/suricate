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
import {FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {AuthenticationService} from '../../authentication.service';
import {HttpConfigurationService} from '../../../../shared/services/api/http-configuration.service';
import {ApplicationProperties} from '../../../../shared/model/api/ApplicationProperties';
import {AuthenticationProviderEnum} from '../../../../shared/model/enums/AuthenticationProviderEnum';
import {FormService} from '../../../../shared/services/app/form.service';
import {FormField} from '../../../../shared/model/app/form/FormField';
import {DataType} from '../../../../shared/model/enums/DataType';

/**
 * Manage the login page
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  /**
   * The login form
   * @type {FormGroup}
   */
  loginForm: FormGroup;
  /**
   * The description of the form
   */
  formFields: FormField[];
  /**
   * Used for display spinner when form has been submitted
   * @type {boolean}
   */
  formSubmitAttempt = false;
  /**
   * True if the user provider is LDAP
   */
  isLdapServerUserProvider: boolean;

  /**
   * Constructor
   *
   * @param {Router} router The router service
   * @param {AuthenticationService} authenticationService The authentication service
   * @param {FormService} formService Generic service used to manage the initiations of forms
   * @param {TranslateService} translateService The translate service
   * @param {HttpConfigurationService} httpConfigurationService The configuration service to inject
   */
  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private formService: FormService,
              private translateService: TranslateService,
              private httpConfigurationService: HttpConfigurationService) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.httpConfigurationService.getAuthenticationProvider().subscribe((applicationProperties: ApplicationProperties) => {
      this.isLdapServerUserProvider = applicationProperties.value.toUpperCase() === AuthenticationProviderEnum.LDAP;
    });

    this.authenticationService.logout();
    this.initLoginForm();
  }

  /**
   * Init the form
   */
  initLoginForm() {
    this.generateFormFields();
    this.loginForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form fields used for the form creation
   */
  generateFormFields() {
    this.translateService.get(['username', 'password']).subscribe((translations: string) => {
      this.formFields = [
        {
          key: 'username',
          label: translations['username'],
          type: DataType.TEXT,
          value: '',
          validators: [Validators.required],
          matIconPrefix: 'android'
        },
        {
          key: 'password',
          label: translations['password'],
          type: DataType.PASSWORD,
          value: '',
          validators: [Validators.required],
          matIconPrefix: 'lock'
        }
      ];
    });
  }

  /**
   * Execute login action
   */
  login() {
    this.formService.validate(this.loginForm);

    if (this.loginForm.valid) {
      // Display spinner
      this.formSubmitAttempt = true;

      // Try to authenticate
      this.authenticationService.authenticate(this.loginForm.value).subscribe(
        () => {
          // Authentication succeed
          this.router.navigate(['/home']);
        },
        () => {
          // Authentication failed
          this.formSubmitAttempt = false;
        });
    }
  }
}
