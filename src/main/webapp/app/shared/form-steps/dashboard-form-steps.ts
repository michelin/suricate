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
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { FormField } from '../models/frontend/form/form-field';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { CustomValidators } from 'ng2-validation';
import { forkJoin, Observable } from 'rxjs';
import { FormStep } from '../models/frontend/form/form-step';
import { IconEnum } from '../enums/icon.enum';

/**
 * Generate the different steps for the creation or edition of a dashboard
 */
@Injectable({ providedIn: 'root' })
export class DashboardFormStepsService {
  /**
   * Constructor
   *
   * @param translateService NGX Translate service used to manage the translations
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Get the list of steps for a dashboard
   */
  public generateDashboardSteps(): Observable<FormStep[]> {
    return forkJoin([this.generateGeneralInformationStep(), this.generateUserManagementStep()]);
  }

  /**
   * The general information step
   */
  private generateGeneralInformationStep(): Observable<FormStep> {
    return this.translateService.get(['dashboard.name', 'widget.heigth.px', 'grid.nb.columns']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'name',
            label: translations['dashboard.name'],
            type: DataTypeEnum.TEXT,
            value: null,
            validators: [Validators.required]
          },
          {
            key: 'widgetHeight',
            label: translations['widget.heigth.px'],
            type: DataTypeEnum.NUMBER,
            // value: this.project ? this.project.gridProperties.widgetHeight : 360,
            value: null,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          },
          {
            key: 'maxColumn',
            label: translations['grid.nb.columns'],
            type: DataTypeEnum.NUMBER,
            // value: this.project ? this.project.gridProperties.maxColumn : 5,
            value: null,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          }
        ];

        return { key: 'first', title: 'General Information', icon: IconEnum.GENERAL_INFORMATION, fields: formFields };
      })
    );
  }

  /**
   * The user management step
   */
  private generateUserManagementStep(): Observable<FormStep> {
    return this.translateService.get(['username.search']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'username',
            label: translations['username.search'],
            type: DataTypeEnum.TEXT,
            value: '',
            options: [],
            hint: translations['username.search'],
            matIconPrefix: 'person_pin',
            validators: [Validators.required]
          }
        ];

        return { key: 'second', title: 'User management', icon: IconEnum.USERS, fields: formFields };
      })
    );
  }
}
