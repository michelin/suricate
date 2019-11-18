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
import { User } from '../models/backend/user/user';

/**
 * Service used to build the form fields related to project users
 */
@Injectable({ providedIn: 'root' })
export class ProjectUsersFormFieldsService {
  /**
   * Constructor
   *
   * @param translateService Ngx translate service used to manage the translations
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Get the list of steps for a dashboard
   *
   * @param users The list of users already added to the project
   */
  public generateProjectUsersFormFields(users?: User[]): Observable<FormField[]> {
    return this.translateService.get(['username']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'username',
            label: translations['username'],
            type: DataTypeEnum.TEXT,
            value: null
          },
          {
            key: 'users',
            label: 'Users',
            type: DataTypeEnum.FIELDS,
            value: null,
            values: users,
            fields: [
              {
                key: 'username',
                label: translations['username'],
                type: DataTypeEnum.TEXT,
                value: null,
                readOnly: true
              },
              {
                key: 'firstname',
                label: 'Firstname',
                type: DataTypeEnum.TEXT,
                value: null,
                readOnly: true
              },
              {
                key: 'lastname',
                label: 'lastname',
                type: DataTypeEnum.TEXT,
                value: null,
                readOnly: true
              }
            ]
          }
        ];
      })
    );
  }
}
