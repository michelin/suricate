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
import { FormsModule, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';

import { Buttons } from '../../../shared/components/buttons/buttons';
import { Input } from '../../../shared/components/inputs/input/input';
import { Spinner } from '../../../shared/components/spinner/spinner';
import { AuthenticationProvider } from '../../../shared/enums/authentication-provider.enum';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { Credentials } from '../../../shared/models/backend/user/credentials';
import { UserRequest } from '../../../shared/models/backend/user/user-request';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { HttpConfigurationService } from '../../../shared/services/backend/http-configuration/http-configuration.service';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { RegisterFormFieldsService } from '../../../shared/services/frontend/form-fields/register-form-fields/register-form-fields.service';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { CustomValidator } from '../../../shared/validators/custom-validator';

/**
 * Component used to register a new user
 */
@Component({
	selector: 'suricate-register',
	templateUrl: './register.html',
	styleUrls: ['./register.scss'],
	imports: [NgOptimizedImage, Spinner, FormsModule, ReactiveFormsModule, Input, Buttons]
})
export class Register implements OnInit {
	private readonly router = inject(Router);
	private readonly httpConfigurationService = inject(HttpConfigurationService);
	private readonly authenticationService = inject(AuthenticationService);
	private readonly formService = inject(FormService);
	private readonly toastService = inject(ToastService);

	/**
	 * The register form
	 */
	public registerForm: UntypedFormGroup;

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
	 * Called when the component is init
	 */
	public ngOnInit(): void {
		if (AuthenticationService.isLoggedIn()) {
			this.navigateToHomePage();
			return;
		}

		this.httpConfigurationService
			.getAuthenticationProviders()
			.subscribe((authenticationProviders: AuthenticationProvider[]) => {
				if (authenticationProviders.indexOf(AuthenticationProvider.LDAP) > -1) {
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
				.signup(userRequest)
				.pipe(
					mergeMap(() => {
						const credentials: Credentials = { username: userRequest.username, password: userRequest.password };
						return this.authenticationService.authenticate(credentials);
					}),
					catchError((error) => {
						return throwError(() => error);
					})
				)
				.subscribe({
					next: () => this.navigateToHomePage(),
					error: (error: HttpErrorResponse) => {
						this.loading = false;
						this.toastService.sendMessage(error.error.key, ToastTypeEnum.DANGER);
					}
				});
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
