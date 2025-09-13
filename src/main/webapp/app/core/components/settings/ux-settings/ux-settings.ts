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

import { Component, inject, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { from, mergeMap } from 'rxjs';
import { toArray } from 'rxjs/operators';

import { Buttons } from '../../../../shared/components/buttons/buttons';
import { Input } from '../../../../shared/components/inputs/input/input';
import { Icon } from '../../../../shared/enums/icon';
import { AllowedSettingValue } from '../../../../shared/models/backend/setting/allowed-setting-value';
import { Setting } from '../../../../shared/models/backend/setting/setting';
import { UserSetting } from '../../../../shared/models/backend/setting/user-setting';
import { UserSettingRequest } from '../../../../shared/models/backend/setting/user-setting-request';
import { ButtonConfiguration } from '../../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { HttpUserService } from '../../../../shared/services/backend/http-user/http-user.service';
import { AuthenticationService } from '../../../../shared/services/frontend/authentication/authentication.service';
import { FormService } from '../../../../shared/services/frontend/form/form.service';
import { SettingsFormFieldsService } from '../../../../shared/services/frontend/form-fields/settings-form-fields/settings-form-fields.service';
import { SettingsService } from '../../../services/settings-service';

@Component({
	selector: 'suricate-ux-settings',
	templateUrl: './ux-settings.html',
	styleUrls: ['./ux-settings.scss'],
	imports: [Input, FormsModule, ReactiveFormsModule, Buttons]
})
export class UxSettings implements OnInit {
	private readonly settingsService = inject(SettingsService);
	private readonly settingsFormFieldsService = inject(SettingsFormFieldsService);
	private readonly formService = inject(FormService);
	private readonly httpUserService = inject(HttpUserService);

	/**
	 * The form group for UX settings
	 */
	public formGroup: UntypedFormGroup;

	/**
	 * The form fields for UX settings
	 */
	public formFields: FormField[];

	/**
	 * The buttons
	 */
	public buttons: ButtonConfiguration<unknown>[] = [];

	/**
	 * The user settings
	 */
	public userSettings: UserSetting[];

	/**
	 * Init method
	 */
	ngOnInit(): void {
		this.initButtons();

		this.settingsService
			.initUserSettings(AuthenticationService.getConnectedUser())
			.subscribe((userSettings: UserSetting[]) => {
				this.userSettings = userSettings;
				this.settingsFormFieldsService.generateSettingsFormFields(userSettings).subscribe((formFields: FormField[]) => {
					this.formFields = formFields;
					this.formGroup = this.formService.generateFormGroupForFields(formFields);
				});
			});
	}

	/**
	 * Init the buttons
	 */
	private initButtons(): void {
		this.buttons.push({
			label: 'save',
			icon: Icon.SAVE,
			callback: () => this.save()
		});
	}

	/**
	 * Execute save action on click
	 */
	private save(): void {
		this.formService.validate(this.formGroup);

		if (this.formGroup.valid) {
			this.saveSettings();
		}
	}

	/**
	 * Save the selected settings
	 */
	private saveSettings(): void {
		from(this.userSettings.map((userSetting) => userSetting.setting))
			.pipe(
				mergeMap((setting: Setting) => {
					const userSettingRequest = new UserSettingRequest();
					if (setting.constrained && setting.allowedSettingValues) {
						const selectedAllowedSetting = setting.allowedSettingValues.find(
							(allowedSettingValue: AllowedSettingValue) => {
								return allowedSettingValue.value === this.formGroup.get(setting.type).value;
							}
						);

						userSettingRequest.allowedSettingValueId = selectedAllowedSetting.id;
					} else {
						userSettingRequest.unconstrainedValue = this.formGroup.get(setting.type).value;
					}

					return this.httpUserService.updateUserSetting(
						AuthenticationService.getConnectedUser().username,
						setting.id,
						userSettingRequest
					);
				}),
				toArray()
			)
			.subscribe(() => {
				this.settingsService
					.initUserSettings(AuthenticationService.getConnectedUser())
					.subscribe((userSettings: UserSetting[]) => {
						this.userSettings = userSettings;
					});
			});
	}
}
