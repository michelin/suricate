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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';

@Injectable()
export abstract class AbstractHttpService {

  public static readonly BASE_URL = 'http://localhost:8080';
  public static readonly BASE_API_URL = `${AbstractHttpService.BASE_URL}/api`;
  public static readonly BASE_WS_URL = `${AbstractHttpService.BASE_URL}/ws`;
  public static readonly AUTHENTICATE_URL = 'oauth/token';
  public static readonly SPRING_ACCESS_TOKEN_ENPOINT = 'access_token';
  public static readonly USERS_URL = 'users';
  public static readonly PROJECTS_URL = 'projects';
  public static readonly ASSET_URL = 'asset';
  public static readonly WIDGETS_URL = 'widgets';
  public static readonly CONFIGURATIONS_URL = 'configurations';

}

