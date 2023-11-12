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
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { AuthenticationProvider } from '../../../shared/enums/authentication-provider.enum';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { LoginFormFieldsService } from '../../../shared/services/frontend/form-fields/login-form-fields/login-form-fields.service';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { SettingsService } from '../../services/settings.service';
import { HttpConfigurationService } from '../../../shared/services/backend/http-configuration/http-configuration.service';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';

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
   */
  public loginForm: UntypedFormGroup;

  /**
   * Fields used to describe/create the form
   */
  public formFields: FormField[];

  /**
   * The list of buttons to display in the form login
   */
  public buttonConfigurations: ButtonConfiguration<unknown>[];

  /**
   * The activated authentication providers
   */
  public authenticationProviders: AuthenticationProvider[];

  /**
   * Define if the spinner should be running or not
   */
  public loading = true;

  /**
   * OAuth2 authentication with GitHub endpoint
   */
  public githubAuthenticationEndpoint = AuthenticationService.GITHUB_AUTH_URL;

  /**
   * OAuth2 authentication with GitLab endpoint
   */
  public gitlabAuthenticationEndpoint = AuthenticationService.GITLAB_AUTH_URL;

  /**
   * Constructor
   * @param router Service used to manage the application routes
   * @param route Service used to manage the activated route
   * @param httpConfigurationService Service used to manage http calls for configurations
   * @param authenticationService Service used to manage authentications
   * @param formService Front-End service used to manage forms
   * @param settingsService Front-End service used to manage the user's settings
   * @param toastService The toast service
   * @param translateService The translate service
   */
  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly httpConfigurationService: HttpConfigurationService,
    private readonly authenticationService: AuthenticationService,
    private readonly formService: FormService,
    private readonly settingsService: SettingsService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {}

  /**
   * Init method
   */
  public ngOnInit(): void {
    const token: string = this.route.snapshot.queryParamMap.get('token');
    const error: string = this.route.snapshot.queryParamMap.get('error');
    if (token) {
      AuthenticationService.setAccessToken(token);
    } else if (error) {
      this.toastService.sendMessage('authentication.failed.with.providers', ToastTypeEnum.DANGER, error);
    }

    if (AuthenticationService.isLoggedIn()) {
      this.navigateToHomePage();
      return;
    }

    this.httpConfigurationService.getAuthenticationProviders().subscribe((authProviders: AuthenticationProvider[]) => {
      this.authenticationProviders = authProviders;
    });

    this.initButtons();
    this.initLoginForm();

    this.loading = false;
  }

  /**
   * Execute login action
   */
  public login(): void {
    this.formService.validate(this.loginForm);

    if (this.loginForm.valid) {
      this.loading = true;

      this.authenticationService.authenticate(this.loginForm.value).subscribe(
        () => this.navigateToHomePage(),
        (error: HttpErrorResponse) => {
          this.loading = false;
          this.toastService.sendMessage(error.error.key, ToastTypeEnum.DANGER);
        }
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
   * Is the database authentication activated or not
   */
  public isDatabaseAuthenticationActivated(): boolean {
    return this.authenticationProviders && this.authenticationProviders.indexOf(AuthenticationProvider.DATABASE) > -1;
  }

  /**
   * Is the LDAP authentication activated or not
   */
  public isLdapAuthenticationActivated(): boolean {
    return this.authenticationProviders && this.authenticationProviders.indexOf(AuthenticationProvider.LDAP) > -1;
  }

  /**
   * Is any social authentication activated or not
   */
  public isSocialLoginActivated(): boolean {
    return this.isGithubAuthenticationActivated() || this.isGitlabAuthenticationActivated();
  }

  /**
   * Is the GitLab authentication activated or not
   */
  public isGitlabAuthenticationActivated(): boolean {
    return this.authenticationProviders && this.authenticationProviders.indexOf(AuthenticationProvider.GITLAB) > -1;
  }

  /**
   * Is the GitHub authentication activated or not
   */
  public isGithubAuthenticationActivated(): boolean {
    return this.authenticationProviders && this.authenticationProviders.indexOf(AuthenticationProvider.GITHUB) > -1;
  }
}
