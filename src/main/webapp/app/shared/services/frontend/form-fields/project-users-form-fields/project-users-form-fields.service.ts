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
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { IconEnum } from '../../../../enums/icon.enum';
import { PageModel } from '../../../../models/backend/page-model';
import { User } from '../../../../models/backend/user/user';
import { FormField } from '../../../../models/frontend/form/form-field';
import { FormOption } from '../../../../models/frontend/form/form-option';
import { HttpFilterService } from '../../../backend/http-filter/http-filter.service';
import { HttpProjectService } from '../../../backend/http-project/http-project.service';
import { HttpUserService } from '../../../backend/http-user/http-user.service';

/**
 * Service used to build the form fields related to project users
 */
@Injectable({ providedIn: 'root' })
export class ProjectUsersFormFieldsService {
  /**
   * Constructor
   *
   * @param translateService Ngx translate service used to manage the translations
   * @param httpProjectService Service used to manage the projects
   * @param httpUserService Service used to manage http calls for user
   */
  constructor(
    private readonly translateService: TranslateService,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpUserService: HttpUserService
  ) {}

  /**
   * Generate the configuration between a dashboard and the associated users.
   * Generate the autocomplete window information.
   * Get the associated users and generate the fields information.
   *
   * @param projectToken The project token used to retrieve the users
   */
  public generateProjectUsersFormFields(projectToken: string): FormField[] {
    return [
      {
        key: 'usernameAutocomplete',
        label: 'username',
        iconPrefix: IconEnum.USER_ADD,
        type: DataTypeEnum.TEXT,
        options: (usernameFilter: string) => this.getUsersAutocomplete(usernameFilter)
      },
      {
        key: 'users',
        label: 'user.list',
        type: DataTypeEnum.FIELDS,
        values: this.httpProjectService.getProjectUsers(projectToken),
        deleteRow: {
          attribute: 'id',
          callback: (userId: number) => this.httpProjectService.deleteUserFromProject(projectToken, userId)
        },
        fields: [
          {
            key: 'id',
            label: 'id',
            type: DataTypeEnum.HIDDEN
          },
          {
            key: 'username',
            label: 'username',
            type: DataTypeEnum.TEXT,
            readOnly: true
          },
          {
            key: 'firstname',
            label: 'firstname',
            type: DataTypeEnum.TEXT,
            readOnly: true
          },
          {
            key: 'lastname',
            label: 'lastname',
            type: DataTypeEnum.TEXT,
            readOnly: true
          }
        ]
      }
    ];
  }

  /**
   * Generate the autocomplete window to link a user with a dashboard
   *
   * @param usernameFilter A filter on the username
   */
  private getUsersAutocomplete(usernameFilter: string): Observable<FormOption[]> {
    return this.httpUserService.getAll(HttpFilterService.getDefaultFilter(usernameFilter)).pipe(
      map((usersPaged: PageModel<User>) => {
        const formOptions: FormOption[] = [];
        usersPaged.content.forEach((user: User) => {
          formOptions.push({
            label: `${user.firstname} ${user.lastname} (${user.username})`,
            value: user.username
          });
        });

        return formOptions;
      })
    );
  }
}
