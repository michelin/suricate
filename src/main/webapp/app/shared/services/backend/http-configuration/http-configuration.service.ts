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
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { AuthenticationProvider } from '../../../enums/authentication-provider.enum';

/**
 * Configuration services manage http calls
 */
@Injectable({ providedIn: 'root' })
export class HttpConfigurationService {
  /**
   * Global configurations endpoint
   */
  private static readonly configurationsApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/configurations`;

  /**
   * Constructor
   * @param httpClient The http client service
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Get the authentication providers authorized by the Back-End
   */
  public getAuthenticationProviders(): Observable<AuthenticationProvider[]> {
    const url = `${HttpConfigurationService.configurationsApiEndpoint}/authentication-providers`;

    return this.httpClient.get<AuthenticationProvider[]>(url);
  }
}
