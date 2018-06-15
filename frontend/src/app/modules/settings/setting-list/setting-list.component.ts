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
import {UserService} from '../../user/user.service';
import {User} from '../../../shared/model/dto/user/User';
import {SettingDataType} from '../../../shared/model/dto/enums/SettingDataType';
import {Observable} from 'rxjs/Observable';
import {ToastService} from '../../../shared/components/toast/toast.service';
import {ToastType} from '../../../shared/model/toastNotification/ToastType';

@Component({
  selector: 'app-setting-list',
  templateUrl: './setting-list.component.html',
  styleUrls: ['./setting-list.component.css']
})
export class SettingListComponent implements OnInit {

  /**
   * The current user
   */
  currentUser$: Observable<User>;

  /**
   * The setting data types
   *
   * @type {SettingDataType}
   */
  settingDataType = SettingDataType;

  /**
   * Constructor
   *
   * @param {UserService} _userService The user service to inject
   * @param {ToastService} _toastService The toast notification service
   */
  constructor(private _userService: UserService,
              private _toastService: ToastService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.currentUser$ = this._userService.connectedUserSubject.asObservable();
    this._userService.getConnectedUser().subscribe();
  }

  /**
   * Save the user settings
   *
   * @param {NgForm} formSettings The form filled
   */
  saveUserSettings(formSettings: NgForm) {
    if (formSettings.valid) {
      const userSettingForm: FormGroup = formSettings.form;
      const currentUser = this._userService.connectedUserSubject.getValue();

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

      this._userService
          .updateUserSettings(currentUser, userSettings)
          .subscribe(user => {
            this._toastService.sendMessage('Settings saved succesfully', ToastType.SUCCESS);
            this._userService.setUserSettings(user);
          });
    }
  }
}
