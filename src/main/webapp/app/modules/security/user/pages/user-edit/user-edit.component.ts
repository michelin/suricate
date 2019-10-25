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
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { flatMap, map } from 'rxjs/operators';
import { CustomValidators } from 'ng2-validation';
import { Observable } from 'rxjs';

import { ToastService } from '../../../../../shared/services/frontend/toast.service';
import { RoleService } from '../../role.service';
import { User } from '../../../../../shared/models/backend/user/user';
import { Role } from '../../../../../shared/models/backend/role/role';
import { ToastTypeEnum } from '../../../../../shared/enums/toast-type.enum';
import { HttpRoleService } from '../../../../../shared/services/backend/http-role.service';
import { HttpUserService } from '../../../../../shared/services/backend/http-user.service';
import { RoleEnum } from '../../../../../shared/enums/role.enum';
import { UserRequest } from '../../../../../shared/models/backend/user/user-request';
import { DataTypeEnum } from '../../../../../shared/enums/data-type.enum';
import { FormService } from '../../../../../shared/services/frontend/form.service';
import { FormStep } from '../../../../../shared/models/frontend/form/form-step';
import { FormField } from '../../../../../shared/models/frontend/form/form-field';
import { FormOption } from '../../../../../shared/models/frontend/form/form-option';

/**
 * Component user the edition of a user
 */
@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit {
  /**
   * The form group
   * @type {FormGroup}
   */
  editUserForm: FormGroup;
  /**
   * The description of the form
   */
  formSteps: FormStep[];
  /**
   * The user to edit
   * @type {User}
   */
  user: User;
  /**
   * The list of roles
   * @type {Role[]}
   */
  roles: Role[];

  /**
   * Constructor
   *
   * @param {httpUserService} httpUserService The user service
   * @param {Router} router The router service to inject
   * @param {RoleService} roleService The role service to inject
   * @param {HttpRoleService} httpRoleService The http role service to inject
   * @param {ActivatedRoute} activatedRoute The activated route to inject
   * @param {FormService} formService The form service used to create the form
   * @param {TranslateService} translateService The translation service
   * @param {ToastService} toastService The service used for displayed Toast notification
   */
  constructor(
    private httpUserService: HttpUserService,
    private router: Router,
    private roleService: RoleService,
    private httpRoleService: HttpRoleService,
    private activatedRoute: ActivatedRoute,
    private formService: FormService,
    private translateService: TranslateService,
    private toastService: ToastService
  ) {}

  /**
   * Called when the component is displayed
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      // Retrieve the user to edit
      this.httpUserService
        .getById(params['userId'])
        .pipe(
          // Retrieve the list of roles
          flatMap((user: User) => {
            this.user = user;
            return this.httpRoleService.getRoles();
          }),
          map((roles: Role[]) => (this.roles = roles))
        )
        .subscribe(() => {
          this.initUserEditForm();
        });
    });
  }

  /**
   * Init the user edit form
   */
  initUserEditForm() {
    this.formSteps = [];

    this.generateStepOne()
      .pipe(
        map((stepOne: FormStep) => (this.formSteps[0] = stepOne)),
        flatMap(() => this.generateStepTwo()),
        map((stepTwo: FormStep) => (this.formSteps[1] = stepTwo))
      )
      .subscribe(() => {
        this.editUserForm = this.formService.generateFormGroupForSteps(this.formSteps);
      });
  }

  /**
   * Generate the step one of the form
   */
  generateStepOne(): Observable<FormStep> {
    return this.translateService.get(['username', 'firstname', 'lastname', 'email']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'username',
            label: translations['username'],
            type: DataTypeEnum.TEXT,
            value: this.user.username,
            readOnly: true,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'android'
          },
          {
            key: 'firstname',
            label: translations['firstname'],
            type: DataTypeEnum.TEXT,
            value: this.user.firstname,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'lastname',
            label: translations['lastname'],
            type: DataTypeEnum.TEXT,
            value: this.user.lastname,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'email',
            label: translations['email'],
            type: DataTypeEnum.TEXT,
            value: this.user.email,
            validators: [Validators.required, CustomValidators.email],
            matIconPrefix: 'email'
          }
        ];

        return { fields: formFields };
      })
    );
  }

  /**
   * Generate the step two of the form
   */
  generateStepTwo(): Observable<FormStep> {
    return this.translateService.get(['roles']).pipe(
      map((translations: string) => {
        // Role Options generation
        const roleOptions: FormOption[] = [];
        this.roles.forEach((role: Role) => {
          roleOptions.push({
            key: role.name,
            label: role.description
          });
        });

        // Creation of the formFields
        const formFields: FormField[] = [
          {
            key: 'roles',
            label: translations['roles'],
            type: DataTypeEnum.MULTIPLE,
            value: this.user.roles.map(role => role.name),
            options: roleOptions,
            validators: [Validators.required]
          }
        ];

        return { fields: formFields };
      })
    );
  }

  /**
   * Save a user
   */
  saveUser() {
    this.formService.validate(this.editUserForm);

    if (this.editUserForm.valid) {
      const userUpdateRequest: UserRequest = this.editUserForm.value;
      userUpdateRequest.roles = [];

      const rolesSelected: RoleEnum[] = this.editUserForm.get('roles').value;
      rolesSelected.forEach((roleName: RoleEnum) => {
        const roleSelected = this.roles.find((role: Role) => role.name === roleName);
        if (roleSelected) {
          userUpdateRequest.roles.push(roleSelected);
        }
      });

      this.httpUserService.updateUser(this.user.id, userUpdateRequest).subscribe(() => {
        this.toastService.sendMessage('User saved successfully', ToastTypeEnum.SUCCESS);
        this.redirectToUserList();
      });
    }
  }

  /**
   * Redirect to the user list after editing succesfully
   */
  redirectToUserList() {
    this.router.navigate(['/security/users']);
  }
}
