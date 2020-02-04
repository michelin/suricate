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
import { FormGroup, Validators } from '@angular/forms';
import { WidgetConfiguration } from '../models/backend/widget-configuration/widget-configuration';
import { IconEnum } from '../enums/icon.enum';
import { HttpCategoryService } from '../services/backend/http-category.service';
import { FormService } from '../services/frontend/form.service';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class WidgetConfigurationFormFieldsService {
  /**
   * Constructor
   */
  constructor(private readonly categoryService: HttpCategoryService, private readonly formService: FormService) {}

  /**
   * Get the list of steps for a dashboard
   *
   * @param configuration The project used for an edition
   */
  public generateFormFields(configuration?: WidgetConfiguration): FormField[] {
    return [
      {
        key: 'key',
        label: 'key',
        type: DataTypeEnum.TEXT,
        value: configuration ? configuration.key : null,
        readOnly: true,
        iconPrefix: IconEnum.KEY
      },
      {
        key: 'category',
        label: 'category',
        type: DataTypeEnum.TEXT,
        value: configuration.category ? configuration.category.name : null,
        readOnly: true,
        iconPrefix: IconEnum.WIDGET
      },
      {
        key: 'value',
        label: 'value',
        type: configuration.dataType,
        value: configuration ? configuration.value : null,
        iconPrefix: IconEnum.VALUE,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Generate an array of form fields for the given widget configuration
   *
   * @param configurations The widget settings
   */
  public generateWidgetConfigurationFormFields(configurations?: WidgetConfiguration[]): FormField[] {
    const formFields: Array<FormField> = [];

    configurations.forEach((configuration, index) => {
      formFields.push({
        key: configuration.key,
        label: configuration.key,
        type: configuration.dataType,
        value: configuration ? configuration.value : null,
        iconPrefix: IconEnum.VALUE,
        validators: [Validators.required]
      });
    });

    return formFields;
  }

  /**
   * Add or remove widget's category fields & controls to the given form
   *
   * @param categoryId The category ID from which retrieve the settings
   * @param checked If yes, add the fields & controls to the given form, otherwise, remove them. Matches to the slide toggle button activation.
   * @param formGroup The form group to which controls will be added
   * @param fields A field array to which new fields will be added
   */
  generateCategorySettingsFormFields(categoryId: number, checked: boolean, formGroup: FormGroup, fields: FormField[]): void {
    this.categoryService.getCategoryConfigurations(categoryId).subscribe(value => {
      const categorySettingsFormFields = this.generateWidgetConfigurationFormFields(value);

      if (checked) {
        fields.push(...categorySettingsFormFields);
        this.formService.addControlsToFormGroupForFields(formGroup, categorySettingsFormFields);
      } else {
        for (const categoryField of categorySettingsFormFields) {
          const index = fields.findIndex(field => field.key === categoryField.key);

          if (index !== -1) {
            fields.splice(index, 1);
          }
        }

        this.formService.removeControlsToFormGroupForFields(formGroup, categorySettingsFormFields);
      }
    });
  }
}
