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

import { inject, Injectable } from '@angular/core';
import { UntypedFormGroup, Validators } from '@angular/forms';

import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { IconEnum } from '../../../../enums/icon.enum';
import { CategoryParameter } from '../../../../models/backend/category-parameters/category-parameter';
import { FormField } from '../../../../models/frontend/form/form-field';
import { FormService } from '../../form/form.service';
import { ProjectWidgetFormStepsService } from '../../form-steps/project-widget-form-steps/project-widget-form-steps.service';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class WidgetConfigurationFormFieldsService {
	private readonly formService = inject(FormService);
	private readonly projectWidgetFormStepsService = inject(ProjectWidgetFormStepsService);

	/**
	 * Get the list of steps for a dashboard
	 *
	 * @param configuration The project used for an edition
	 */
	public generateFormFields(configuration?: CategoryParameter): FormField[] {
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
				iconSuffix: configuration.dataType === DataTypeEnum.PASSWORD ? IconEnum.SHOW_PASSWORD : undefined,
				validators: [Validators.required]
			}
		];
	}

	/**
	 * Generate an array of form fields for the given category parameters
	 *
	 * @param categorySettings The widget settings
	 * @param widgetBackendConfig The current widget backend configuration
	 */
	public generateCategoryParametersFormFields(
		categorySettings: CategoryParameter[],
		widgetBackendConfig: string
	): FormField[] {
		const formFields: FormField[] = [];

		categorySettings.forEach((configuration) => {
			let backendConfigValue = null;

			if (widgetBackendConfig) {
				backendConfigValue = this.projectWidgetFormStepsService.retrieveProjectWidgetValueFromConfig(
					configuration.key,
					widgetBackendConfig
				);
			}

			formFields.push({
				key: configuration.key,
				label: configuration.description,
				type: configuration.dataType,
				value: backendConfigValue || configuration.value,
				iconPrefix: IconEnum.VALUE,
				iconSuffix: configuration.dataType === DataTypeEnum.PASSWORD ? IconEnum.SHOW_PASSWORD : undefined,
				validators: [Validators.required]
			});
		});

		return formFields;
	}

	/**
	 * Add or remove widget's category fields & controls to the given form.
	 *
	 * @param categorySettings The information about the settings of the category
	 * @param checked If yes, add the fields & controls to the given form, otherwise, remove them. Matches to the slide toggle button activation.
	 * @param formGroup The form group to which controls will be added
	 * @param fields A field array to which new fields will be added
	 * @param widgetBackendConfig The current widget backend configuration
	 */
	public addOrRemoveCategoryParametersFormFields(
		categorySettings: CategoryParameter[],
		checked: boolean,
		formGroup: UntypedFormGroup,
		fields: FormField[],
		widgetBackendConfig?: string
	): void {
		const categorySettingsFormFields = this.generateCategoryParametersFormFields(categorySettings, widgetBackendConfig);

		if (checked) {
			fields.unshift(...categorySettingsFormFields);
			this.formService.addControlsToFormGroupForFields(formGroup, categorySettingsFormFields);
		} else {
			for (const categoryField of categorySettingsFormFields) {
				const index = fields.findIndex((field) => field.key === categoryField.key);

				if (index !== -1) {
					fields.splice(index, 1);
				}
			}

			this.formService.removeControlsToFormGroupForFields(formGroup, categorySettingsFormFields);
		}
	}
}
