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
import { FormField } from '../../../../models/frontend/form/form-field';
import { Setting } from '../../../../models/backend/setting/setting';
import { EMPTY, forkJoin, from, Observable, of } from 'rxjs';
import { HttpSettingService } from '../../../backend/http-setting/http-setting.service';
import { HttpUserService } from '../../../backend/http-user/http-user.service';
import { AuthenticationService } from '../../authentication/authentication.service';
import { UserSetting } from '../../../../models/backend/setting/user-setting';
import { flatMap, map, toArray } from 'rxjs/operators';
import { FormOption } from '../../../../models/frontend/form/form-option';
import { AllowedSettingValue } from '../../../../models/backend/setting/allowed-setting-value';
import { IconEnum } from '../../../../enums/icon.enum';

/**
 * Service used to build the form fields related to the settings
 */
@Injectable({ providedIn: 'root' })
export class SettingsFormFieldsService {
  /**
   * Constructor
   *
   * @param {HttpSettingService} httpSettingService Suricate service used to manage http calls for settings
   * @param {HttpUserService} httpUserService Suricate service used to manage http calls for users
   */
  constructor(private readonly httpSettingService: HttpSettingService, private readonly httpUserService: HttpUserService) {}

  /**
   * Get the list of fields for the settings
   */
  public generateFormFields(): Observable<FormField[]> {
    return this.httpUserService.getUserSettings(AuthenticationService.getConnectedUser().username).pipe(
      flatMap((userSettings: UserSetting[]) => {
        return from(userSettings).pipe(
          flatMap((userSetting: UserSetting) => {
            return forkJoin({
              userSetting: of(userSetting),
              setting: this.httpSettingService.getOneById(userSetting.settingId)
            });
          }),
          map((userSettingForkJoin: { userSetting: UserSetting; setting: Setting }) => {
            return {
              key: userSettingForkJoin.setting.type,
              label: userSettingForkJoin.setting.description,
              iconPrefix: IconEnum[userSettingForkJoin.setting.type],
              type: userSettingForkJoin.setting.dataType,
              value: userSettingForkJoin.userSetting.settingValue.value,
              options: () => this.generateOptions(userSettingForkJoin.setting)
            };
          }),
          toArray()
        );
      })
    );
  }

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
