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

import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { from } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

import { SettingsService } from '../../services/settings.service';
import { User } from '../../../shared/models/backend/user/user';
import { HttpUserService } from '../../../shared/services/backend/http-user.service';
import { UserSetting } from '../../../shared/models/backend/setting/user-setting';
import { HttpSettingService } from '../../../shared/services/backend/http-setting.service';
import { Setting } from '../../../shared/models/backend/setting/setting';
import { SettingsTypeEnum } from '../../../shared/enums/settings-type.enum';
import { AllowedSettingValue } from '../../../shared/models/backend/setting/allowed-setting-value';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { FormService } from '../../../shared/services/frontend/form.service';
import { FormOption } from '../../../shared/models/frontend/form/form-option';

/**
 * Represent the Admin Setting list page
 */
@Component({
  selector: 'app-settings-list',
  templateUrl: './settings-list.component.html',
  styleUrls: ['./settings-list.component.scss']
})
export class SettingsListComponent implements OnInit {
  /**
   * The user setting form
   * @type {FormGroup}
   */
  userSettingForm: FormGroup;
  /**
   * The description of the form
   */
  formFields: FormField[];

  /**
   * The connected user
   * @type {User}
   */
  connectedUser: User;

  /**
   * The list of user settings
   * @type {UserSetting[]}
   */
  userSettings: UserSetting[];

  /**
   * The list of settings
   */
  settings: Setting[];

  /**
   * The setting data types
   * @type {DataTypeEnum}
   */
  dataType = DataTypeEnum;

  /**
   * Constructor
   *
   * @param {HttpUserService} httpUserService The http user service
   * @param {HttpSettingService} httpSettingService The http setting service
   * @param {SettingsService} settingsService The settings service to inject
   * @param {TranslateService} translateService The service used for translations
   * @param {FormService} formService The form service used for the form creation
   */
  constructor(
    private httpUserService: HttpUserService,
    private httpSettingService: HttpSettingService,
    private settingsService: SettingsService,
    private translateService: TranslateService,
    private formService: FormService
  ) {}

  /**
   * When the component is init
   */
  ngOnInit(): void {
    // Get the connected user
    this.httpUserService
      .getConnectedUser()
      .pipe(
        // Get the related userSettings
        flatMap((connectedUser: User) => {
          this.connectedUser = connectedUser;
          return this.httpUserService.getUserSettings(connectedUser.id);
        }),
        // Get the full list of settings
        flatMap((userSettings: UserSetting[]) => {
          this.userSettings = userSettings;
          return this.httpSettingService.getAll();
        }),
        map((settings: Setting[]) => (this.settings = settings))
      )
      .subscribe(() => {
        // When we have every objects needed we can create the form
        this.initUserSettingForm();
      });
  }

  /**
   * Init the user setting form
   */
  initUserSettingForm() {
    this.generateFormFields();
    this.userSettingForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form fields used for the form creation
   */
  generateFormFields() {
    this.formFields = [];

    this.settings.forEach((setting: Setting) => {
      const formField: FormField = {
        key: setting.type,
        label: setting.description,
        type: setting.dataType,
        value: this.getUserSettingFromSetting(setting.id).settingValue.value
      };

      if (setting.allowedSettingValues) {
        const formOptions: FormOption[] = [];
        setting.allowedSettingValues.forEach((allowedSettingValue: AllowedSettingValue) => {
          formOptions.push({
            key: allowedSettingValue.value,
            label: allowedSettingValue.title
          });
        });

        formField.options = formOptions;
      }

      this.formFields.push(formField);
    });
  }

  /**
   * Get a user setting from a setting
   *
   * @param settingId The setting id to find
   */
  getUserSettingFromSetting(settingId: number): UserSetting {
    return this.userSettings.find(userSetting => userSetting.settingId === settingId);
  }

  /**
   * Save the user settings
   */
  saveUserSettings() {
    this.formService.validate(this.userSettingForm);

    if (this.userSettingForm.valid) {
      console.log(this.userSettingForm.value);
      from(this.settings)
        .pipe(
          flatMap(setting => {
            if (setting.constrained) {
              const selectedFormValue = this.userSettingForm.get(setting.type);
              const allowedSettingValue = this.getAllowedSettingValueFromSettingTypeAndAllowedSettingValue(
                setting.type,
                selectedFormValue.value
              );

              return this.httpUserService.updateUserSetting(this.connectedUser.id, setting.id, {
                allowedSettingValueId: allowedSettingValue.id
              });
            }
          })
        )
        .subscribe(() => {
          this.settingsService.initUserSettings(this.connectedUser);
        });
    }
  }

  /**
   * Get an allowed setting value by settingType and a allowed value as string
   *
   * @param settingType The setting type
   * @param allowedSettingValue The allowed setting value as String
   */
  getAllowedSettingValueFromSettingTypeAndAllowedSettingValue(
    settingType: SettingsTypeEnum,
    allowedSettingValue: string
  ): AllowedSettingValue {
    return this.settings
      .find(setting => setting.type === settingType)
      .allowedSettingValues.find(allowedSetting => allowedSetting.value === allowedSettingValue);
  }
}
