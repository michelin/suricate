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
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserSetting } from '../../shared/models/backend/setting/user-setting';
import { User } from '../../shared/models/backend/user/user';
import { HttpUserService } from '../../shared/services/backend/http-user/http-user.service';
import { SettingsTypeEnum } from '../../shared/enums/settings-type.enum';
import { HttpSettingService } from '../../shared/services/backend/http-setting/http-setting.service';

/**
 * Manage the app theme
 */
@Injectable({ providedIn: 'root' })
export class SettingsService {
  /**
   * Hold the current theme
   *
   * @type {BehaviorSubject<string>}
   * @private
   */
  private currentThemeSubject = new Subject<string>();

  /**
   * Constructor
   *
   * @param {TranslateService} translateService The translate service to inject
   * @param {HttpSettingService} httpSettingService The http setting service
   * @param {HttpUserService} httpUserService The http user service to inject
   */
  constructor(
    private translateService: TranslateService,
    private httpSettingService: HttpSettingService,
    private httpUserService: HttpUserService
  ) {}

  /* ************************************************************************ */
  /*                Global Part                                                */

  /* ************************************************************************ */

  /**
   * When any user is not connected
   */
  initDefaultSettings() {
    this.initDefaultThemeSetting();
    this.initDefaultLanguageSettings();
  }

  /**
   * init the user settings at connection
   * @param {User} user The user use for set the settings
   */
  initUserSettings(user: User) {
    this.initUserThemeSetting(user);
    this.initLanguageUserSettings(user);
  }

  /* ************************************************************************ */
  /*                Theme Part                                                */

  /* ************************************************************************ */

  /**
   * The current theme as observable
   *
   * @returns {Observable<string>}
   */
  getThemeChangingMessages(): Observable<string> {
    return this.currentThemeSubject.asObservable();
  }

  /**
   * Set the new theme
   *
   * @param {string} themeName
   */
  set currentTheme(themeName: string) {
    this.currentThemeSubject.next(themeName);
  }

  /**
   * Get the template user setting
   *
   * @param {User} user The user
   * @returns {Observable<UserSetting>} The user setting as observable
   */
  getThemeUserSetting(user: User): Observable<UserSetting> {
    return this.httpSettingService
      .getAll(SettingsTypeEnum.TEMPLATE)
      .pipe(flatMap(settings => this.httpUserService.getUserSetting(user.username, settings[0].id)));
  }

  /**
   * Init the theme user settings
   * @param {User} user The user
   */
  initUserThemeSetting(user: User) {
    if (user) {
      this.getThemeUserSetting(user).subscribe(userSetting => {
        this.currentTheme = userSetting.settingValue.value;
      });
    }
  }

  /**
   * Init the default theme settings
   */
  initDefaultThemeSetting() {
    this.httpSettingService.getAll(SettingsTypeEnum.TEMPLATE).subscribe(settings => {
      this.currentTheme = settings[0].allowedSettingValues.find(allowedSettingValue => allowedSettingValue.default).value;
    });
  }

  /* ************************************************************************ */
  /*                Language Part                                             */

  /* ************************************************************************ */

  /**
   * Init the plugin language settings
   */
  initDefaultLanguageSettings() {
    this.httpSettingService.getAll(SettingsTypeEnum.LANGUAGE).subscribe(
      settings => {
        const defaultLanguageCode = settings[0].allowedSettingValues.find(allowedSettingValue => allowedSettingValue.default).value;

        // this language will be used as a fallback when a translation isn't found in the current language
        this.translateService.setDefaultLang(defaultLanguageCode);
        // the lang to use, if the lang isn't available, it will use the current loader to get them
        this.translateService.use(defaultLanguageCode);
      },
      () => {
        this.translateService.setDefaultLang('en');
        this.translateService.use('en');
      }
    );
  }

  /**
   * Init the user settings for the language part
   *
   * @param {User} user The user
   */
  initLanguageUserSettings(user: User) {
    if (user) {
      this.getLanguageUserSetting(user).subscribe(userSetting => {
        this.translateService.use(userSetting.settingValue.value);
      });
    }
  }

  /**
   * Get the template user setting
   *
   * @param {User} user The user
   * @returns {Observable<UserSetting>} The user setting as observable
   */
  getLanguageUserSetting(user: User): Observable<UserSetting> {
    return this.httpSettingService
      .getAll(SettingsTypeEnum.LANGUAGE)
      .pipe(flatMap(settings => this.httpUserService.getUserSetting(user.username, settings[0].id)));
  }
}
