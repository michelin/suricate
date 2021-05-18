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
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { UserSetting } from '../../shared/models/backend/setting/user-setting';
import { User } from '../../shared/models/backend/user/user';
import { HttpUserService } from '../../shared/services/backend/http-user/http-user.service';
import { SettingsTypeEnum } from '../../shared/enums/settings-type.enum';
import { HttpSettingService } from '../../shared/services/backend/http-setting/http-setting.service';
import { Setting } from '../../shared/models/backend/setting/setting';

/**
 * Manage the app theme
 */
@Injectable({ providedIn: 'root' })
export class SettingsService {
  /**
   * Theme of the user
   */
  private currentThemeValueSubject = new Subject<string>();

  /**
   * Constructor
   *
   * @param translateService The translate service to inject
   * @param httpSettingService The http setting service
   * @param httpUserService The http user service to inject
   */
  constructor(
    private translateService: TranslateService,
    private httpSettingService: HttpSettingService,
    private httpUserService: HttpUserService
  ) {}

  /**
   * Init default settings when any user is connected
   */
  initDefaultSettings() {
    this.httpSettingService.getAll().subscribe((settings: Setting[]) => {
      this.currentThemeValue = settings
        .find(setting => setting.type === SettingsTypeEnum.THEME)
        .allowedSettingValues.find(allowedSettingValue => allowedSettingValue.default).value;

      const defaultLanguageCode = settings
        .find(setting => setting.type === SettingsTypeEnum.LANGUAGE)
        .allowedSettingValues.find(allowedSettingValue => allowedSettingValue.default).value;

      // This language will be used as a fallback when a translation is not found in the current language
      this.translateService.setDefaultLang(defaultLanguageCode);

      this.translateService.use(defaultLanguageCode);
    });
  }

  /**
   * Init the user settings
   *
   * @param user The user
   */
  initUserSettings(user: User): Observable<UserSetting[]> {
    return this.httpUserService.getUserSettings(user.username).pipe(
      tap((userSettings: UserSetting[]) => {
        this.currentThemeValue = userSettings.find(userSetting => userSetting.setting.type === SettingsTypeEnum.THEME).settingValue.value;

        this.translateService.use(
          userSettings.find(userSetting => userSetting.setting.type === SettingsTypeEnum.LANGUAGE).settingValue.value
        );
      })
    );
  }

  /**
   * Save the current theme value
   *
   * @param theme The theme value
   */
  set currentThemeValue(theme: string) {
    this.currentThemeValueSubject.next(theme);
  }

  /**
   * Current theme value as observable
   */
  getCurrentThemeValue(): Observable<string> {
    return this.currentThemeValueSubject.asObservable();
  }
}
