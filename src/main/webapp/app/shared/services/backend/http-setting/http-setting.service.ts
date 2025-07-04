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

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Setting } from '../../../models/backend/setting/setting';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';

/**
 * Manage the setting http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpSettingService {
  private static readonly settingsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/settings`;
  private readonly httpClient = inject(HttpClient);

  /**
   * Get the list of settings
   */
  public getAll(): Observable<Setting[]> {
    const url = `${HttpSettingService.settingsApiEndpoint}`;

    return this.httpClient.get<Setting[]>(url);
  }
}
