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
import { FormField } from '../models/frontend/form/form-field';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { Configuration } from '../models/backend/configuration/configuration';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class WidgetConfigurationFormFieldsService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Get the list of steps for a dashboard
   *
   * @param configuration The project used for an edition
   */
  public generateFormFields(configuration?: Configuration): FormField[] {
    return [
      {
        key: 'key',
        label: 'key',
        type: DataTypeEnum.TEXT,
        value: configuration ? configuration.key : null,
        readOnly: true,
        matIconPrefix: 'vpn_key'
      },
      {
        key: 'category',
        label: 'configuration.category',
        type: DataTypeEnum.TEXT,
        value: configuration.category ? configuration.category.name : null,
        readOnly: true,
        matIconPrefix: 'widgets'
      },
      {
        key: 'value',
        label: 'value',
        type: configuration.dataType,
        value: configuration ? configuration.value : null,
        matIconPrefix: 'input',
        validators: [Validators.required]
      }
    ];
  }
}
