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

import {Component, OnInit} from '@angular/core';
import {FormGroup, NgForm} from '@angular/forms';

import {UserService} from '../../../security/user/user.service';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {SettingsService} from '../../settings.service';
import {User} from '../../../../shared/model/api/user/User';
import {HttpUserService} from '../../../../shared/services/api/http-user.service';
import {SettingDataType} from '../../../../shared/model/enums/SettingDataType';
import {UserSetting} from '../../../../shared/model/api/setting/UserSetting';
import {HttpSettingService} from '../../../../shared/services/api/http-setting.service';
import {Setting} from '../../../../shared/model/api/setting/Setting';
import {SettingType} from '../../../../shared/model/enums/SettingType';
import {AllowedSettingValue} from '../../../../shared/model/api/setting/AllowedSettingValue';
import {from} from 'rxjs';
import {flatMap} from 'rxjs/operators';

/**
 * Represent the Admin Setting list page
 */
@Component({
  selector: 'app-settings-list',
  templateUrl: './settings-list.component.html',
  styleUrls: ['./settings-list.component.css']
})
export class SettingsListComponent implements OnInit {

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
   * @type {SettingDataType}
   */
  settingDataType = SettingDataType;

  /**
   * Constructor
   *
   * @param {HttpUserService} httpUserService The http user service
   * @param {HttpSettingService} httpSettingService The http setting service
   * @param {UserService} userService The user service to inject
   * @param {ToastService} toastService The toast notification service
   * @param {SettingsService} settingsService The settings service to inject
   */
  constructor(private httpUserService: HttpUserService,
              private httpSettingService: HttpSettingService,
              private userService: UserService,
              private toastService: ToastService,
              private settingsService: SettingsService) {
  }

  /**
   * When the component is init
   */
  ngOnInit(): void {
    this.httpUserService.getConnectedUser().subscribe(connectedUser => {
      this.connectedUser = connectedUser;
      this.refreshUserSettings();
    });

    this.httpSettingService.getAll().subscribe(settings => {
      this.settings = settings;
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
   * Method used to refresh the user settings
   */
  refreshUserSettings(): void {
    this.httpUserService.getUserSettings(this.connectedUser.id).subscribe(userSettings => {
      this.userSettings = userSettings;
    });
  }

  /**
   * Get an allowed setting value by settingType and a allowed value as string
   *
   * @param settingType The setting type
   * @param allowedSettingValue The allowed setting value as String
   */
  getAllowedSettingValueFromSettingTypeAndAllowedSettingValue(settingType: SettingType, allowedSettingValue: string): AllowedSettingValue {
    return this.settings.find(setting => setting.type === settingType)
      .allowedSettingValues.find(allowedSetting => allowedSetting.value === allowedSettingValue);
  }

  /**
   * Save the user settings
   *
   * @param {NgForm} formSettings The form filled
   */
  saveUserSettings(formSettings: NgForm) {
    if (formSettings.valid) {
      const userSettingForm: FormGroup = formSettings.form;

      console.log(userSettingForm.value);
      from(this.settings).pipe(
        flatMap(setting => {
          if (setting.constrained) {
            const selectedFormValue = userSettingForm.get(setting.type);
            const allowedSettingValue = this.getAllowedSettingValueFromSettingTypeAndAllowedSettingValue(setting.type, selectedFormValue.value);

            return this.httpUserService.updateUserSetting(this.connectedUser.id, setting.id, {allowedSettingValueId: allowedSettingValue.id});
          }
        })
      ).subscribe(() => {
        this.settingsService.initUserSettings(this.connectedUser);
      });

    }
  }
}
