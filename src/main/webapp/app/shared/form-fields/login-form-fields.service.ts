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
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { FormField } from '../models/frontend/form/form-field';
import { IconEnum } from '../enums/icon.enum';

/**
 * Service used to build the form fields related to the login page
 */
@Injectable({ providedIn: 'root' })
export class LoginFormFieldsService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Get the list of fields for the login page
   */
  public static generateFormFields(): FormField[] {
    return [
      {
        key: 'username',
        label: 'username',
        type: DataTypeEnum.TEXT,
        validators: [Validators.required],
        iconPrefix: IconEnum.USERNAME
      },
      {
        key: 'password',
        label: 'password',
        type: DataTypeEnum.PASSWORD,
        validators: [Validators.required],
        iconPrefix: IconEnum.PASSWORD
      }
    ];
  }
}
