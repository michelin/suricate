/*
 * Copyright 2012-2021 the original author or authors.
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

import { Component, Input, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { Router } from '@angular/router';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { SettingsFormFieldsService } from '../../../shared/services/frontend/form-fields/settings-form-fields/settings-form-fields.service';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { from } from 'rxjs';
import { flatMap, toArray } from 'rxjs/operators';
import { Setting } from '../../../shared/models/backend/setting/setting';
import { HttpUserService } from '../../../shared/services/backend/http-user/http-user.service';
import { HttpSettingService } from '../../../shared/services/backend/http-setting/http-setting.service';
import { UserSettingRequest } from '../../../shared/models/backend/setting/user-setting-request';
import { AllowedSettingValue } from '../../../shared/models/backend/setting/allowed-setting-value';
import { SettingsService } from '../../../core/services/settings.service';
import { UserSetting } from '../../../shared/models/backend/setting/user-setting';
import { User } from '../../../shared/models/backend/user/user';
import { MenuConfiguration } from '../../../shared/models/frontend/menu/menu-configuration';

/**
 * Display the menu on the sidenav
 */
@Component({
  selector: 'suricate-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  /**
   * The user connected
   */
  public readonly connectedUser = AuthenticationService.getConnectedUser();

  /**
   * The menu to display
   */
  public readonly menu = MenuService.buildMenu();

  /**
   * User settings
   */
  private userSettings: UserSetting[];

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param router Angular service used to manage routes
   * @param httpSettingService Suricate service used to manage settings
   * @param httpUserService Suricate service used to manage user
   * @param sidenavService Frontend service used to manage sidenavs
   * @param settingsFormFieldsService Frontend service used to build form fields for settings management
   * @param settingsService Frontend service used to manage settings
   * @param authenticationService The authentication service
   */
  constructor(
    private readonly router: Router,
    private readonly httpSettingService: HttpSettingService,
    private readonly httpUserService: HttpUserService,
    private readonly sidenavService: SidenavService,
    private readonly settingsFormFieldsService: SettingsFormFieldsService,
    private readonly settingsService: SettingsService,
    private readonly authenticationService: AuthenticationService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe((userSettings: UserSetting[]) => {
      this.userSettings = userSettings;
    });
  }

  /**
   * Get the initials of the connected user
   */
  public getInitials(): string {
    return this.connectedUser.firstname && this.connectedUser.lastname
      ? `${this.connectedUser.firstname.substring(0, 1)}${this.connectedUser.lastname.substring(0, 1)}`
      : '';
  }

  /**
   * Open the form sidenav used to manage user settings
   */
  public openSettingsFormSidenav(): void {
    this.settingsFormFieldsService.generateSettingsFormFields(this.userSettings).subscribe((formFields: FormField[]) => {
      this.sidenavService.openFormSidenav({
        title: 'settings',
        formFields: formFields,
        save: (formData: FormData) => this.saveSettings(formData)
      });
    });
  }

  /**
   * Save the selected settings
   *
   * @param formData The selected settings from the form
   */
  private saveSettings(formData: FormData): void {
    from(this.userSettings.map(userSetting => userSetting.setting))
      .pipe(
        flatMap((setting: Setting) => {
          const userSettingRequest = new UserSettingRequest();
          if (setting.constrained && setting.allowedSettingValues) {
            const selectedAllowedSetting = setting.allowedSettingValues.find((allowedSettingValue: AllowedSettingValue) => {
              return allowedSettingValue.value === formData[setting.type];
            });

            userSettingRequest.allowedSettingValueId = selectedAllowedSetting.id;
          } else {
            userSettingRequest.unconstrainedValue = formData[setting.type];
          }

          return this.httpUserService.updateUserSetting(AuthenticationService.getConnectedUser().username, setting.id, userSettingRequest);
        }),
        toArray()
      )
      .subscribe(() => {
        this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe((userSettings: UserSetting[]) => {
          this.userSettings = userSettings;
        });
      });
  }

  /**
   * Logout the user
   */
  public logout(): void {
    AuthenticationService.logout();
    this.router.navigate(['/login']);
  }
}
