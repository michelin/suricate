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
import {CustomValidators} from 'ng2-validation';
import {catchError, flatMap} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

import {AuthenticationService} from '../../authentication.service';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ApplicationProperties} from '../../../../shared/model/api/ApplicationProperties';
import {HttpConfigurationService} from '../../../../shared/services/api/http-configuration.service';
import {Credentials} from '../../../../shared/model/api/user/Credentials';
import {ToastType} from '../../../../shared/components/toast/toast-objects/ToastType';
import {UserRequest} from '../../../../shared/model/api/user/UserRequest';
import {AuthenticationProviderEnum} from '../../../../shared/model/enums/AuthenticationProviderEnum';
import {FormField} from '../../../../shared/model/app/form/FormField';
import {DataType} from '../../../../shared/model/enums/DataType';
import {FormService} from '../../../../shared/services/app/form.service';
import {CustomValidator} from '../../../../shared/validators/CustomValidator';


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
   * @param {HttpConfigurationService} httpConfigurationService The configuration service to inject
   */
  constructor(private authenticationService: AuthenticationService,
              private router: Router,
              private toastService: ToastService,
              private translateService: TranslateService,
              private formService: FormService,
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
    this.generateFormFields();
    this.registerForm = this.formService.generateFormGroupForFields(this.formFields);
    this.formService.setValidatorsForControl(
      this.registerForm.get('confirmPassword'),
      [
        Validators.required,
        Validators.minLength(3),
        CustomValidator.checkPasswordMatch(this.registerForm.get('password'))
      ]
    );
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
            type: DataType.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'android'
          },
          {
            key: 'firstname',
            label: translations['firstname'],
            type: DataType.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'lastname',
            label: translations['lastname'],
            type: DataType.TEXT,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'email',
            label: translations['email'],
            type: DataType.TEXT,
            value: '',
            validators: [Validators.required, CustomValidators.email],
            matIconPrefix: 'email'
          },
          {
            key: 'password',
            label: translations['password'],
            type: DataType.PASSWORD,
            value: '',
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'lock'
          },
          {
            key: 'confirmPassword',
            label: translations['password.confirm'],
            type: DataType.PASSWORD,
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
