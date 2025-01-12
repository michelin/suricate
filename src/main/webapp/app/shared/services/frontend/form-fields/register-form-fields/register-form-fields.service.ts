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

import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { IconEnum } from '../../../../enums/icon.enum';
import { FormField } from '../../../../models/frontend/form/form-field';

/**
 * Service used to build the form fields related to the register page
 */
@Injectable({ providedIn: 'root' })
export class RegisterFormFieldsService {
  /**
   * Get the list of fields for the register page
   */
  public static generateFormFields(): FormField[] {
    return [
      {
        key: 'username',
        label: 'username',
        type: DataTypeEnum.TEXT,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USERNAME
      },
      {
        key: 'firstname',
        label: 'firstname',
        type: DataTypeEnum.TEXT,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USER
      },
      {
        key: 'lastname',
        label: 'lastname',
        type: DataTypeEnum.TEXT,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.USER
      },
      {
        key: 'email',
        label: 'email',
        type: DataTypeEnum.TEXT,
        validators: [Validators.required, Validators.email],
        iconPrefix: IconEnum.EMAIL
      },
      {
        key: 'password',
        label: 'password',
        type: DataTypeEnum.PASSWORD,
        validators: [Validators.required, Validators.minLength(3)],
        iconPrefix: IconEnum.PASSWORD,
        iconSuffix: IconEnum.SHOW_PASSWORD
      },
      {
        key: 'confirmPassword',
        label: 'password.confirm',
        type: DataTypeEnum.PASSWORD,
        iconPrefix: IconEnum.PASSWORD,
        iconSuffix: IconEnum.SHOW_PASSWORD
      }
    ];
  }
}
