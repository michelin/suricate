/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../../shared/models/frontend/button/button-configuration';
import { UserSetting } from '../../../../shared/models/backend/setting/user-setting';
import { AuthenticationService } from '../../../../shared/services/frontend/authentication/authentication.service';
import { IconEnum } from '../../../../shared/enums/icon.enum';
import { from, mergeMap } from 'rxjs';
import { toArray } from 'rxjs/operators';
import { Setting } from '../../../../shared/models/backend/setting/setting';
import { UserSettingRequest } from '../../../../shared/models/backend/setting/user-setting-request';
import { AllowedSettingValue } from '../../../../shared/models/backend/setting/allowed-setting-value';
import { SettingsService } from '../../../services/settings.service';
import {
  SettingsFormFieldsService
} from '../../../../shared/services/frontend/form-fields/settings-form-fields/settings-form-fields.service';
import { FormService } from '../../../../shared/services/frontend/form/form.service';
import { HttpUserService } from '../../../../shared/services/backend/http-user/http-user.service';

@Component({
  selector: 'suricate-ux-settings',
  templateUrl: './ux-settings.component.html',
  styleUrls: ['./ux-settings.component.scss']
})
export class UxSettingsComponent implements OnInit {
  /**
   * The form group for UX settings
   */
  public formGroup: UntypedFormGroup;

  /**
   * The form fields for UX settings
   */
  public formFields: FormField[];

  /**
   * The buttons
   */
  public buttons: ButtonConfiguration<unknown>[] = [];

  /**
   * The user settings
   */
  public userSettings: UserSetting[];

  /**
   * Constructor
   * @param settingsService The settings service
   * @param settingsFormFieldsService The settings form fields service
   * @param formService The form service
   * @param httpUserService The http user service
   */
  constructor(
    private readonly settingsService: SettingsService,
    private readonly settingsFormFieldsService: SettingsFormFieldsService,
    private readonly formService: FormService,
    private readonly httpUserService: HttpUserService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initButtons();

    this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe((userSettings: UserSetting[]) => {
      this.userSettings = userSettings;
      this.settingsFormFieldsService.generateSettingsFormFields(userSettings).subscribe((formFields: FormField[]) => {
        this.formFields = formFields;
        this.formGroup = this.formService.generateFormGroupForFields(formFields);
      });
    });
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.buttons.push({
      label: 'save',
      icon: IconEnum.SAVE,
      color: 'primary',
      callback: () => this.save()
    });
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.formService.validate(this.formGroup);

    if (this.formGroup.valid) {
      this.saveSettings();
    }
  }

  /**
   * Save the selected settings
   */
  private saveSettings(): void {
    from(this.userSettings.map(userSetting => userSetting.setting))
      .pipe(
        mergeMap((setting: Setting) => {
          const userSettingRequest = new UserSettingRequest();
          if (setting.constrained && setting.allowedSettingValues) {
            const selectedAllowedSetting = setting.allowedSettingValues.find((allowedSettingValue: AllowedSettingValue) => {
              return allowedSettingValue.value === this.formGroup.get(setting.type).value;
            });

            userSettingRequest.allowedSettingValueId = selectedAllowedSetting.id;
          } else {
            userSettingRequest.unconstrainedValue = this.formGroup.get(setting.type).value;
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
}
