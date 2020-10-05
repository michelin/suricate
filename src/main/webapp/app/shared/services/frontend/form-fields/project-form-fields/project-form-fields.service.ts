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
import { Project } from '../../../../models/backend/project/project';
import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { FormField } from '../../../../models/frontend/form/form-field';
import { IconEnum } from '../../../../enums/icon.enum';
import { CssService } from '../../css/css.service';
import { CustomValidator } from '../../../../validators/custom-validator';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class ProjectFormFieldsService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Get the list of steps for a dashboard
   *
   * @param project The project used for an edition
   */
  public static generateProjectFormFields(project?: Project): FormField[] {
    const backgroundColor =
      project && project.gridProperties.cssStyle
        ? CssService.extractCssValue(project.gridProperties.cssStyle, '.grid', 'background-color')
        : '#87878700';

    return [
      {
        key: 'name',
        label: 'name',
        iconPrefix: IconEnum.NAME,
        type: DataTypeEnum.TEXT,
        value: project ? project.name : null,
        validators: [Validators.required]
      },
      {
        key: 'widgetHeight',
        label: 'widget.height.px',
        iconPrefix: IconEnum.HEIGHT,
        type: DataTypeEnum.NUMBER,
        value: project ? project.gridProperties.widgetHeight : 360,
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      },
      {
        key: 'maxColumn',
        label: 'column.number',
        iconPrefix: IconEnum.COLUMN,
        type: DataTypeEnum.NUMBER,
        value: project ? project.gridProperties.maxColumn : 5,
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      },
      {
        key: 'gridBackgroundColor',
        label: 'background.color',
        type: DataTypeEnum.COLOR_PICKER,
        value: backgroundColor
      }
    ];
  }
}
