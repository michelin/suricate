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

import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';

import { SettingsType } from '../../shared/enums/settings-type';
import { Setting } from '../../shared/models/backend/setting/setting';
import { UserSetting } from '../../shared/models/backend/setting/user-setting';
import { User } from '../../shared/models/backend/user/user';
import { HttpSettingService } from '../../shared/services/backend/http-setting/http-setting-service';
import { HttpUserService } from '../../shared/services/backend/http-user/http-user-service';

/**
 * Manage the app theme
 */
@Injectable({ providedIn: 'root' })
export class SettingsService {
	private readonly translateService = inject(TranslateService);
	private readonly httpSettingService = inject(HttpSettingService);
	private readonly httpUserService = inject(HttpUserService);

	/**
	 * Theme of the user
	 */
	private readonly currentThemeValueSubject = new Subject<string>();

	/**
	 * Init default settings when any user is connected
	 */
	initDefaultSettings() {
		this.httpSettingService.getAll().subscribe({
			next: (settings: Setting[]) => {
				this.currentThemeValue = settings
					.find((setting) => setting.type === SettingsType.THEME)
					.allowedSettingValues.find((allowedSettingValue) => allowedSettingValue.default).value;

				const defaultLanguageCode = settings
					.find((setting) => setting.type === SettingsType.LANGUAGE)
					.allowedSettingValues.find((allowedSettingValue) => allowedSettingValue.default).value;

				// This language will be used as a fallback when a translation is not found in the current language
				this.translateService.setFallbackLang(defaultLanguageCode);
				this.translateService.use(defaultLanguageCode);
			},
			error: () => {
				this.translateService.setFallbackLang('en');
				this.translateService.use('en');
			}
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
				this.currentThemeValue = userSettings.find(
					(userSetting) => userSetting.setting.type === SettingsType.THEME
				).settingValue.value;

				this.translateService.use(
					userSettings.find((userSetting) => userSetting.setting.type === SettingsType.LANGUAGE).settingValue.value
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
