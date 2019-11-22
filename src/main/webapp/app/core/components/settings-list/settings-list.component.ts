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
import { from, of } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';

import { SettingsService } from '../../services/settings.service';
import { User } from '../../../shared/models/backend/user/user';
import { HttpUserService } from '../../../shared/services/backend/http-user.service';
import { UserSetting } from '../../../shared/models/backend/setting/user-setting';
import { HttpSettingService } from '../../../shared/services/backend/http-setting.service';
import { Setting } from '../../../shared/models/backend/setting/setting';
import { SettingsTypeEnum } from '../../../shared/enums/settings-type.enum';
import { AllowedSettingValue } from '../../../shared/models/backend/setting/allowed-setting-value';
import { FormService } from '../../../shared/services/frontend/form.service';
import { FormOption } from '../../../shared/models/frontend/form/form-option';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { FormField } from '../../../shared/models/frontend/form/form-field';

/**
 * Used to display/manage users preferences
 */
@Component({
  selector: 'suricate-settings-list',
  templateUrl: './settings-list.component.html',
  styleUrls: ['./settings-list.component.scss']
})
export class SettingsListComponent implements OnInit {
  /**
   * The connected user
   * @type {User}
   * @private
   */
  private connectedUser: User;
  /**
   * The list of user settings
   * @type {UserSetting[]}
   * @private
   */
  private userSettings: UserSetting[];
  /**
   * The list of available settings
   * @type {Setting[]}
   * @private
   */
  private settings: Setting[];

  /**
   * Hold the configuration of the header
   * @type {HeaderConfiguration}
   * @protected
   */
  protected headerConfiguration: HeaderConfiguration;
  /**
   * Hold the form
   * @type {FormGroup}
   * @protected
   */
  protected userSettingForm: FormGroup;
  /**
   * The description of the form
   * @type {FormField[]}
   * @protected
   */
  protected formFields: FormField[];

  /**
   * Constructor
   *
   * @param {HttpUserService} httpUserService Suricate service used to manage http calls for users
   * @param {HttpSettingService} httpSettingService Suricate service used to manage http calls for settings
   * @param {SettingsService} settingsService Frontend service used to manage settings in App
   * @param {FormService} formService Frontend service used to manage forms
   */
  constructor(
    private readonly httpUserService: HttpUserService,
    private readonly httpSettingService: HttpSettingService,
    private readonly settingsService: SettingsService,
    private readonly formService: FormService
  ) {
    this.initHeaderConfiguration();
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
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
   * Used to init the configuration of the header
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'user.settings' };
  }

  /**
   * Init the user setting form
   */
  private initUserSettingForm(): void {
    this.generateFormFields();
    this.userSettingForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form fields used for the form creation
   */
  private generateFormFields(): void {
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
            label: allowedSettingValue.title,
            value: allowedSettingValue.value
          });
        });

        formField.options = () => of(formOptions);
      }

      this.formFields.push(formField);
    });
  }

  /**
   * Get The user setting related to a setting
   *
   * @param settingId The setting id to find
   */
  private getUserSettingFromSetting(settingId: number): UserSetting {
    return this.userSettings.find(userSetting => userSetting.settingId === settingId);
  }

  /**
   * Save the user settings
   */
  private saveUserSettings(): void {
    this.formService.validate(this.userSettingForm);

    if (this.userSettingForm.valid) {
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
  private getAllowedSettingValueFromSettingTypeAndAllowedSettingValue(
    settingType: SettingsTypeEnum,
    allowedSettingValue: string
  ): AllowedSettingValue {
    return this.settings
      .find(setting => setting.type === settingType)
      .allowedSettingValues.find(allowedSetting => allowedSetting.value === allowedSettingValue);
  }
}
