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
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
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
   *
   * @param translateService Ngx translate service used to manage the translations
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Get the list of steps for a dashboard
   *
   * @param project The project used for an edition
   */
  public generateFormFields(configuration?: Configuration): Observable<FormField[]> {
    return this.translateService.get(['key', 'configuration.category', 'value']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'key',
            label: translations['key'],
            type: DataTypeEnum.TEXT,
            value: configuration ? configuration.key : null,
            readOnly: true,
            matIconPrefix: 'vpn_key'
          },
          {
            key: 'category',
            label: translations['configuration.category'],
            type: DataTypeEnum.TEXT,
            value: configuration.category ? configuration.category.name : null,
            readOnly: true,
            matIconPrefix: 'widgets'
          },
          {
            key: 'value',
            label: translations['value'],
            type: configuration.dataType,
            value: configuration ? configuration.value : null,
            matIconPrefix: 'input',
            validators: [Validators.required]
          }
        ];
      })
    );
  }
}
