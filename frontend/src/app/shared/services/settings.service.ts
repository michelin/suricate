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
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {TranslateService} from '@ngx-translate/core';
import {UserSetting} from '../model/dto/UserSetting';
import {User} from '../model/dto/user/User';
import {SettingType} from '../model/dto/enums/SettingType';

/**
 * Manage the app theme
 */
@Injectable()
export class SettingsService {

  /**
   * Hold the current theme
   *
   * @type {BehaviorSubject<string>}
   * @private
   */
  private _currentTheme$ = new BehaviorSubject<string>('default-theme');

  /**
   * Constructor
   *
   * @param {TranslateService} _translateService The translate service to inject
   */
  constructor(private _translateService: TranslateService,) {
  }

  /* ************************************************************************ */
  /*                Global Part                                                */

  /* ************************************************************************ */

  /**
   * init the user settings at connection
   * @param {User} user The user use for set the settings
   */
  initUserSettings(user: User) {
    this.setTheme(this.getThemeUserSetting(user).settingValue.value);
    this.initLanguageSettings();
  }

  /**
   * set the user settings
   *
   * @param {User} user The user used to set the settings
   */
  setUserSettings(user: User) {
    this.setTheme(this.getThemeUserSetting(user).settingValue.value);
    // Add language here
  }

  /* ************************************************************************ */
  /*                Theme Part                                                */

  /* ************************************************************************ */

  /**
   * The current theme as observable
   * @returns {Observable<string>}
   */
  getCurrentTheme(): Observable<string> {
    return this._currentTheme$.asObservable();
  }

  /**
   * Set the new theme
   *
   * @param {string} themeName
   */
  setTheme(themeName: string) {
    this._currentTheme$.next(themeName);
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

  /* ************************************************************************ */
  /*                Language Part                                             */

  /* ************************************************************************ */

  /**
   * Init the plugin language settings
   */
  initLanguageSettings() {
    // this language will be used as a fallback when a translation isn't found in the current language
    this._translateService.setDefaultLang('en');

    // the lang to use, if the lang isn't available, it will use the current loader to get them
    this._translateService.use('en');
  }

  /**
   * Switch the language
   *
   * @param {string} language The new language
   */
  switchLanguage(language: string) {
    this._translateService.use(language);
  }
}
