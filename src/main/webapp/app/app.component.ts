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

import { Component, HostBinding, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { OverlayContainer } from '@angular/cdk/overlay';
import { takeWhile } from 'rxjs/operators';

import { AuthenticationService } from './core/services/authentication.service';
import { SettingsService } from './core/services/settings.service';
import { UserService } from './admin/services/user.service';

@Component({
  selector: 'suricate-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
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
  private isAlive = true;

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
   * @param {AuthenticationService} authenticationService Authentication service to inject
   * @param {OverlayContainer} overlayContainer The overlay container service
   * @param {UserService} userService The user service
   * @param {SettingsService} settingsService The settings service to inject
   */
  constructor(
    private authenticationService: AuthenticationService,
    private overlayContainer: OverlayContainer,
    private userService: UserService,
    private settingsService: SettingsService
  ) {}

  /**
   * Called at the init of the app
   */
  ngOnInit() {
    this.isLoggedIn$ = this.authenticationService.isLoggedIn$.pipe(takeWhile(() => this.isAlive));
    this.settingsService.currentTheme$.pipe(takeWhile(() => this.isAlive)).subscribe(themeValue => {
      this.switchTheme(themeValue);
    });

    this.settingsService.initDefaultSettings();
    this.userService.connectedUser$.subscribe(user => {
      if (user) {
        this.settingsService.initUserSettings(user);
      } else {
        this.settingsService.initDefaultSettings();
      }
    });
  }

  /**
   * Switch the theme
   * @param {string} themeValue The new theme value
   */
  switchTheme(themeValue: string) {
    this.overlayContainer.getContainerElement().classList.remove(this.appHtmlClass);
    this.overlayContainer.getContainerElement().classList.add(themeValue);
    this.appHtmlClass = themeValue;
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }
}
