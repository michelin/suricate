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

import { Injectable } from '@angular/core';
import { Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { IconEnum } from '../../../../enums/icon.enum';
import { Page } from '../../../../models/backend/page';
import { Role } from '../../../../models/backend/role/role';
import { User } from '../../../../models/backend/user/user';
import { FormField } from '../../../../models/frontend/form/form-field';
import { FormOption } from '../../../../models/frontend/form/form-option';
import { HttpFilterService } from '../../../backend/http-filter/http-filter.service';
import { HttpRoleService } from '../../../backend/http-role/http-role.service';

/**
 * Service used to build the form fields related to a user
 */
@Injectable({ providedIn: 'root' })
export class UserFormFieldsService {
  /**
   * Constructor
   *
   * @param httpRoleService Suricate service used to manage http calls for role
   */
  constructor(private readonly httpRoleService: HttpRoleService) {}

  /**
   * Build the form fields of the user
   *
   * @param user The bean
   */
  generateFormFields(user?: User): FormField[] {
    return [
      {
        key: 'username',
        label: 'username',
        type: DataTypeEnum.TEXT,
        value: user.username ? user.username : null,
        readOnly: true,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USERNAME
      },
      {
        key: 'firstname',
        label: 'firstname',
        type: DataTypeEnum.TEXT,
        value: user.firstname ? user.firstname : null,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USER
      },
      {
        key: 'lastname',
        label: 'lastname',
        type: DataTypeEnum.TEXT,
        value: user.lastname ? user.lastname : null,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USER
      },
      {
        key: 'email',
        label: 'email',
        type: DataTypeEnum.TEXT,
        value: user.email ? user.email : null,
        validators: [Validators.required, Validators.email],
        iconPrefix: IconEnum.EMAIL
      },
      {
        key: 'roles',
        label: 'roles',
        type: DataTypeEnum.MULTIPLE,
        value: user.roles && user.roles.length > 0 ? user.roles.map((role) => role.name) : null,
        options: () => this.getRoleOptions(),
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the role options
   */
  getRoleOptions(): Observable<FormOption[]> {
    return this.httpRoleService.getRoles(HttpFilterService.getInfiniteFilter()).pipe(
      map((rolesPaged: Page<Role>) => {
        const roleOptions: FormOption[] = [];
        rolesPaged.content.forEach((role: Role) => {
          roleOptions.push({
            label: role.description,
            value: role.name
          });
        });

        return roleOptions;
      })
    );
  }
}
