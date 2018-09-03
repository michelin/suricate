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
import {Observable} from 'rxjs';

import {UserService} from '../../../security/user/user.service';
import {User} from '../../../../shared/model/dto/user/User';
import {SettingDataType} from '../../../../shared/model/dto/enums/SettingDataType';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../shared/model/toastNotification/ToastType';
import {SettingsService} from '../../../../shared/services/settings.service';

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
   * The current user
   * @type {Observable<User>}
   */
  currentUser$: Observable<User>;

  /**
   * The setting data types
   * @type {SettingDataType}
   */
  settingDataType = SettingDataType;

  /**
   * Constructor
   *
   * @param {UserService} userService The user service to inject
   * @param {ToastService} toastService The toast notification service
   * @param {SettingsService} settingsService The settings service to inject
   */
  constructor(private userService: UserService,
              private toastService: ToastService,
              private settingsService: SettingsService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.currentUser$ = this.userService.connectedUser$;
    this.userService.getConnectedUser().subscribe();
  }

  /**
   * Save the user settings
   *
   * @param {NgForm} formSettings The form filled
   */
  saveUserSettings(formSettings: NgForm) {
    if (formSettings.valid) {
      const userSettingForm: FormGroup = formSettings.form;
      const currentUser = this.userService.connectedUser;

      const userSettings = currentUser.userSettings;
      userSettings.forEach(userSetting => {
        const userValue = userSettingForm.get(userSetting.setting.type).value;

        if (userSetting.setting.constrained) {
          userSetting.settingValue = userSetting.setting.allowedSettingValues.find(allowedSettingValue => {
            return allowedSettingValue.value === userValue;
          });
        } else {
          userSetting.unconstrainedValue = userValue;
        }
      });

      this.userService
          .updateUserSettings(currentUser, userSettings)
          .subscribe(user => {
            this.toastService.sendMessage('Settings saved succesfully', ToastType.SUCCESS);
            this.settingsService.setUserSettings(user);
          });
    }
  }
}
