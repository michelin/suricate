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
import { Validators } from '@angular/forms';
import { EMPTY, from, Observable } from 'rxjs';
import { map, toArray } from 'rxjs/operators';

import { IconEnum } from '../../../../enums/icon.enum';
import { AllowedSettingValue } from '../../../../models/backend/setting/allowed-setting-value';
import { Setting } from '../../../../models/backend/setting/setting';
import { UserSetting } from '../../../../models/backend/setting/user-setting';
import { FormField } from '../../../../models/frontend/form/form-field';
import { FormOption } from '../../../../models/frontend/form/form-option';

/**
 * Service used to build the form fields related to the settings
 */
@Injectable({ providedIn: 'root' })
export class SettingsFormFieldsService {
	/**
	 * Get the list of fields for the settings
	 */
	public generateSettingsFormFields(userSettings: UserSetting[]): Observable<FormField[]> {
		return from(userSettings).pipe(
			map((userSetting: UserSetting) => {
				return {
					key: userSetting.setting.type,
					label: userSetting.setting.description,
					iconPrefix: IconEnum[userSetting.setting.type],
					type: userSetting.setting.dataType,
					value: userSetting.settingValue.value,
					validators: [Validators.required],
					options: () => this.generateOptions(userSetting.setting)
				};
			}),
			toArray()
		);
	}

	/**
	 * Generate the options of the settings
	 *
	 * @param setting The setting
	 * @private A form option
	 */
	private generateOptions(setting: Setting): Observable<FormOption[]> {
		if (setting.allowedSettingValues) {
			return from(setting.allowedSettingValues).pipe(
				map((allowedSettingValue: AllowedSettingValue) => {
					return {
						label: allowedSettingValue.title,
						value: allowedSettingValue.value
					};
				}),
				toArray()
			);
		}

		return EMPTY;
	}
}
