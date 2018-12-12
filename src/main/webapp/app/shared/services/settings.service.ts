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

import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {BehaviorSubject, Observable} from 'rxjs';

import {UserSetting} from '../model/api/setting/UserSetting';
import {User} from '../model/api/user/User';
import {SettingType} from '../model/enums/SettingType';

/**
 * Manage the app theme
 */
@Injectable()
export class SettingsService {

  /**
   * The default theme code
   * @type {string}
   * @private
   */
  private readonly defaultThemeCode = 'default-theme';

  /**
   * The default language code
   * @type {string}
   * @private
   */
  private readonly defaultLanguageCode = 'en';

  /**
   * Hold the current theme
   *
   * @type {BehaviorSubject<string>}
   * @private
   */
  private currentThemeSubject = new BehaviorSubject<string>(this.defaultThemeCode);

  /**
   * Constructor
   *
   * @param {TranslateService} translateService The translate service to inject
   */
  constructor(private translateService: TranslateService) {
  }

  /* ************************************************************************ */
  /*                Global Part                                                */

  /* ************************************************************************ */

  /**
   * When any user is not connected
   */
  initDefaultSettings() {
    this.initDefaultThemeSetting();
    this.initLanguageSettings();
  }

  /**
   * init the user settings at connection
   * @param {User} user The user use for set the settings
   */
  initUserSettings(user: User) {
    this.initUserThemeSetting(user);
  }

  /**
   * set the user settings
   *
   * @param {User} user The user used to set the settings
   */
  setUserSettings(user: User) {
    this.currentTheme = this.getThemeUserSetting(user).settingValue.value;
    // Add language here
  }

  /* ************************************************************************ */
  /*                Theme Part                                                */

  /* ************************************************************************ */

  /**
   * The current theme as observable
   * @returns {Observable<string>}
   */
  get currentTheme$(): Observable<string> {
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
   * @returns {UserSetting} The user setting
   */
  getThemeUserSetting(user: User): UserSetting {
    return user.userSettings.find(userSetting => userSetting.setting.type === SettingType.TEMPLATE);
  }

  /**
   * Init the theme user settings
   * @param {User} user The user
   */
  initUserThemeSetting(user: User) {
    if (user) {
      this.currentTheme = this.getThemeUserSetting(user).settingValue.value;
    } else {
      this.initDefaultThemeSetting();
    }
  }

  /**
   * Init the default theme settings
   */
  initDefaultThemeSetting() {
    this.currentTheme = this.defaultThemeCode;
  }

  /* ************************************************************************ */
  /*                Language Part                                             */

  /* ************************************************************************ */

  /**
   * Init the plugin language settings
   */
  initLanguageSettings() {
    // this language will be used as a fallback when a translation isn't found in the current language
    this.translateService.setDefaultLang(this.defaultLanguageCode);

    // the lang to use, if the lang isn't available, it will use the current loader to get them
    this.translateService.use(this.defaultLanguageCode);
  }

  /**
   * Switch the language
   *
   * @param {string} language The new language
   */
  switchLanguage(language: string) {
    this.translateService.use(language);
  }
}
