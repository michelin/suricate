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

import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthenticationService} from './modules/authentication/authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {SettingsService} from './shared/services/settings.service';
import {UserService} from './modules/user/user.service';
import {OverlayContainer} from '@angular/cdk/overlay';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

  /**
   * The HTML class attribute
   */
  @HostBinding('class') appHtmlClass;

  /**
   * Tell if the component is instantiate or not
   *
   * @type {boolean}
   * @private
   */
  private _isAlive = true;

  /**
   * Observable that tell to the app if the user is connected
   * @type {Observable<boolean>}
   */
  isLoggedIn$: Observable<boolean>;

  /**
   * The title app
   * @type {string}
   */
  title = 'Dashboard - Monitoring';

  /**
   * The constructor
   *
   * @param {AuthenticationService} _authenticationService Authentication service to inject
   * @param {OverlayContainer} overlayContainer The overlay container service
   * @param {UserService} _userService The user service
   * @param {TranslateService} _translateService The translation service
   * @param {SettingsService} _settingsService The settings service to inject
   */
  constructor(private _authenticationService: AuthenticationService,
              private overlayContainer: OverlayContainer,
              private _userService: UserService,
              private _translateService: TranslateService,
              private _settingsService: SettingsService) {
  }

  /**
   * Called at the init of the app
   */
  ngOnInit() {
    this.isLoggedIn$ = this._authenticationService.isLoggedIn$;
    this._settingsService.currentTheme$.subscribe(themeValue => this.switchTheme(themeValue));

    this._settingsService.initDefaultSettings();
    this._userService.connectedUser$.subscribe(user => {
      if (user) {
        this._settingsService.initUserSettings(user);
      } else {
        this._settingsService.initDefaultSettings();
      }
    });
  }

  /**
   * Switch the theme
   * @param {string} themeValue The new theme value
   */
  switchTheme(themeValue: string) {
    this.overlayContainer.getContainerElement().classList.add(themeValue);
    this.appHtmlClass = themeValue;
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this._isAlive = false;
  }
}
