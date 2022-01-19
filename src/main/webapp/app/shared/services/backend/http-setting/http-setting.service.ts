/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Setting } from '../../../models/backend/setting/setting';
import { SettingsTypeEnum } from '../../../enums/settings-type.enum';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Manage the setting http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpSettingService {
  /**
   * Global endpoint for settings
   */
  private static readonly settingsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/settings`;

  /**
   * Constructor
   *
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the list of settings
   */
  public getAll(): Observable<Setting[]> {
    const url = `${HttpSettingService.settingsApiEndpoint}`;

    return this.httpClient.get<Setting[]>(url);
  }
}
