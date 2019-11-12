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
import { Project } from '../models/backend/project/project';
import { map } from 'rxjs/operators';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { CustomValidators } from 'ng2-validation';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class ProjectFormFieldsService {
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
  public generateFormFields(project?: Project): Observable<FormField[]> {
    return this.translateService.get(['dashboard.name', 'widget.heigth.px', 'grid.nb.columns', 'grid.background.color']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'gridBackgroundColor',
            label: translations['grid.background.color'],
            type: DataTypeEnum.COLOR_PICKER,
            value: null
          },
          {
            key: 'name',
            label: translations['dashboard.name'],
            type: DataTypeEnum.TEXT,
            value: project ? project.name : null,
            validators: [Validators.required]
          },
          {
            key: 'widgetHeight',
            label: translations['widget.heigth.px'],
            type: DataTypeEnum.NUMBER,
            value: project ? project.gridProperties.widgetHeight : 360,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          },
          {
            key: 'maxColumn',
            label: translations['grid.nb.columns'],
            type: DataTypeEnum.NUMBER,
            value: project ? project.gridProperties.maxColumn : 5,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          }
        ];
      })
    );
  }
}
