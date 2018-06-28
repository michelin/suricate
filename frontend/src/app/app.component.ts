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
import {OverlayContainer} from '@angular/cdk/overlay';
import {SettingsService} from './shared/services/settings.service';
import {takeWhile} from 'rxjs/operators';
import {UserService} from './modules/user/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

  /**
   * Observable that tell to the app if the user is connected
   */
  isLoggedIn$: Observable<boolean>;

  /**
   * The title app
   * @type {string}
   */
  title = 'Dashboard - Monitoring';

  /**
   * Used for setting the new class
   */
  @HostBinding('class') componentCssClass;

  /**
   * Tell if the component is instantiate or not
   *
   * @type {boolean}
   * @private
   */
  private _isAlive = true;

  /**
   * The constructor
   *
   * @param {AuthenticationService} authenticationService Authentication service to inject
   * @param {OverlayContainer} overlayContainer The overlay container service
   * @param {SettingsService} themeService The theme service to inject
   * @param {UserService} _userService The user service
   * @param {TranslateService} _translateService The translation service
   * @param {SettingsService} _settingsService The settings service to inject
   */
  constructor(private authenticationService: AuthenticationService,
              private overlayContainer: OverlayContainer,
              private themeService: SettingsService,
              private _userService: UserService,
              private _translateService: TranslateService,
              private _settingsService: SettingsService) {
  }

  /**
   * Called at the init of the app
   */
  ngOnInit() {
    this.isLoggedIn$ = this.authenticationService.isLoggedIn();
    this._settingsService.initLanguageSettings();

    this.themeService.getCurrentTheme()
        .pipe(takeWhile(() => this._isAlive))
        .subscribe((themeName: string) => this.onSetTheme(themeName));

    this._userService.getConnectedUser().subscribe(user => this._settingsService.initUserSettings(user));
  }

  /**
   * Set the theme
   *
   * @param theme The theme name
   */
  onSetTheme(theme) {
    this.overlayContainer.getContainerElement().classList.add(theme);
    this.componentCssClass = theme;
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this._isAlive = false;
  }
}
