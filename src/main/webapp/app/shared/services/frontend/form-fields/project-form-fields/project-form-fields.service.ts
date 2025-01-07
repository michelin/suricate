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
import { Project } from '../../../../models/backend/project/project';
import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { FormField } from '../../../../models/frontend/form/form-field';
import { IconEnum } from '../../../../enums/icon.enum';
import { CssService } from '../../css/css.service';
import { CustomValidator } from '../../../../validators/custom-validator';
import { TranslateService } from '@ngx-translate/core';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class ProjectFormFieldsService {
  /**
   * Key of the form field for project name
   */
  public static readonly projectNameFormFieldKey = 'name';

  /**
   * Key of the form field for project widget height
   */
  public static readonly projectWidgetHeightFormFieldKey = 'widgetHeight';

  /**
   * Key of the form field for project max column
   */
  public static readonly projectMaxColumnFormFieldKey = 'maxColumn';

  /**
   * Key of the form field for project image
   */
  public static readonly projectImageFormFieldKey = 'image';

  /**
   * Key of the form field for project image
   */
  public static readonly projectGridBackgroundColorFormFieldKey = 'gridBackgroundColor';

  /**
   * Key of the time form field for grids
   */
  public static readonly timeFormFieldKey = 'time';

  /**
   * Key of the form field for grids progress bar
   */
  public static readonly progressBarFormFieldKey = 'displayProgressBar';

  /**
   * Constructor
   */
  constructor(private translateService: TranslateService) {}

  /**
   * Get the list of form fields for a dashboard
   *
   * @param project The project used for an edition
   */
  public generateProjectFormFields(project?: Project): FormField[] {
    const backgroundColor =
      project && project.gridProperties.cssStyle
        ? CssService.extractCssValue(project.gridProperties.cssStyle, '.grid', 'background-color')
        : '#87878700';

    return [
      {
        key: ProjectFormFieldsService.projectNameFormFieldKey,
        label: 'dashboard.title.form.field',
        iconPrefix: IconEnum.NAME,
        type: DataTypeEnum.TEXT,
        value: project?.name ? project.name : null,
        placeholder: this.translateService.instant('dashboard.title.form.field.placeholder'),
        validators: [Validators.required]
      },
      {
        key: ProjectFormFieldsService.projectWidgetHeightFormFieldKey,
        label: 'row.height.px',
        iconPrefix: IconEnum.HEIGHT,
        type: DataTypeEnum.NUMBER,
        value: project?.gridProperties.widgetHeight ? project.gridProperties.widgetHeight : 360,
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      },
      {
        key: ProjectFormFieldsService.projectMaxColumnFormFieldKey,
        label: 'column.number',
        iconPrefix: IconEnum.COLUMN,
        type: DataTypeEnum.NUMBER,
        value: project?.gridProperties.maxColumn ? project.gridProperties.maxColumn : 5,
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      },
      {
        key: ProjectFormFieldsService.projectImageFormFieldKey,
        label: 'dashboard.upload.logo',
        type: DataTypeEnum.FILE,
        value: project?.image ? `data:${project.image.contentType};base64,${project.image.content}` : undefined,
        validators: [CustomValidator.fileHasFormat()]
      },
      {
        key: ProjectFormFieldsService.projectGridBackgroundColorFormFieldKey,
        label: 'background.color',
        type: DataTypeEnum.COLOR_PICKER,
        value: backgroundColor
      }
    ];
  }

  /**
   * Generate the form field when adding a new grid to the project
   */
  public generateAddGridFormField(): FormField[] {
    return [
      {
        key: `${ProjectFormFieldsService.timeFormFieldKey}`,
        label: `${this.translateService.instant('dashboard.grid.addition.rotation.speed.form.field')}`,
        iconPrefix: IconEnum.SPEED,
        type: DataTypeEnum.TEXT,
        placeholder: this.translateService.instant('dashboard.grid.management.rotation.speed.form.field.placeholder'),
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      }
    ];
  }

  /**
   * Get the list of form fields for the grids management
   *
   * @param project The project used for an edition
   */
  public generateGridsManagementFormFields(project: Project): FormField[] {
    const formFields: FormField[] = [];

    formFields.push({
      key: ProjectFormFieldsService.progressBarFormFieldKey,
      label: 'dashboard.grid.management.progress.bar.form.field',
      iconPrefix: IconEnum.PROGRESS_BAR,
      type: DataTypeEnum.BOOLEAN,
      value: project.displayProgressBar
    });

    project.grids.forEach((grid, index) => {
      formFields.push({
        key: `${ProjectFormFieldsService.timeFormFieldKey}-${index}`,
        label: `${this.translateService.instant('dashboard.grid.management.rotation.speed.form.field')} ${index}`,
        iconPrefix: IconEnum.SPEED,
        type: DataTypeEnum.TEXT,
        value: grid.time,
        placeholder: this.translateService.instant('dashboard.grid.management.rotation.speed.form.field.placeholder'),
        validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0]
      });
    });

    return formFields;
  }
}
