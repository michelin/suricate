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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FormField } from '../models/frontend/form/form-field';
import { map } from 'rxjs/operators';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { FormOption } from '../models/frontend/form/form-option';
import { TranslateService } from '@ngx-translate/core';
import { User } from '../models/backend/user/user';
import { CustomValidators } from 'ng2-validation';
import { Role } from '../models/backend/role/role';

/**
 * Service used to build the form fields related to a user
 */
@Injectable({ providedIn: 'root' })
export class UserFormFieldsService {
  /**
   * Constructor
   *
   * @param translateService Ngx translate service used to manage the translations
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Build the form fields of the user
   *
   * @param roles the full list of roles
   * @param user The bean
   */
  generateFormFields(roles: Role[], user?: User): Observable<FormField[]> {
    return this.translateService.get(['username', 'firstname', 'lastname', 'email', 'roles']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'username',
            label: translations['username'],
            type: DataTypeEnum.TEXT,
            value: user.username ? user.username : null,
            readOnly: true,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'android'
          },
          {
            key: 'firstname',
            label: translations['firstname'],
            type: DataTypeEnum.TEXT,
            value: user.firstname ? user.firstname : null,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'lastname',
            label: translations['lastname'],
            type: DataTypeEnum.TEXT,
            value: user.lastname ? user.lastname : null,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'email',
            label: translations['email'],
            type: DataTypeEnum.TEXT,
            value: user.email ? user.email : null,
            validators: [Validators.required, CustomValidators.email],
            matIconPrefix: 'email'
          },
          {
            key: 'roles',
            label: translations['roles'],
            type: DataTypeEnum.MULTIPLE,
            value: user.roles && user.roles.length > 0 ? user.roles.map(role => role.name) : null,
            options: this.getRoleOptions(roles),
            validators: [Validators.required]
          }
        ];
      })
    );
  }

  /**
   * Get the role options
   *
   * @param roles The full list of roles
   */
  getRoleOptions(roles: Role[]): FormOption[] {
    const roleOptions: FormOption[] = [];
    roles.forEach((role: Role) => {
      roleOptions.push({
        label: role.description,
        value: role.name
      });
    });

    return roleOptions;
  }
}
