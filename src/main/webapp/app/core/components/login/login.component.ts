/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { NgOptimizedImage } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonsComponent } from '../../../shared/components/buttons/buttons.component';
import { InputComponent } from '../../../shared/components/inputs/input/input.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';
import { AuthenticationProvider } from '../../../shared/enums/authentication-provider.enum';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { HttpConfigurationService } from '../../../shared/services/backend/http-configuration/http-configuration.service';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { LoginFormFieldsService } from '../../../shared/services/frontend/form-fields/login-form-fields/login-form-fields.service';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';

/**
 * Manage the login page
 */
@Component({
  selector: 'suricate-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    NgOptimizedImage,
    SpinnerComponent,
    FormsModule,
    ReactiveFormsModule,
    InputComponent,
    ButtonsComponent,
    RouterLink,
    MatDivider,
    TranslatePipe
  ]
})
export class LoginComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly httpConfigurationService = inject(HttpConfigurationService);
  private readonly authenticationService = inject(AuthenticationService);
  private readonly formService = inject(FormService);
  private readonly toastService = inject(ToastService);

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

      this.authenticationService.authenticate(this.loginForm.value).subscribe({
        next: () => this.navigateToHomePage(),
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.toastService.sendMessage(error.error.key, ToastTypeEnum.DANGER);
        }
      });
    }
  }

  /**
   * Initialize the list of buttons to use in the application
   */
  private initButtons(): void {
    this.buttonConfigurations = [
      {
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
