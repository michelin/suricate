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

import {Injectable} from '@angular/core';

/**
 * Hold the common Http informations
 */
@Injectable()
export abstract class AbstractHttpService {

  /**
   * The base API url
   * @type {string}
   */
  public static readonly BASE_API_URL = process.env.BASE_URL + '/api';
  /**
   * The base WS url
   * @type {string}
   */
  public static readonly BASE_WS_URL = process.env.BASE_URL + '/ws';

  /**
   * Global endpoint for Authentication
   * @type {string}
   */
  public static readonly AUTHENTICATE_URL = 'oauth/token';

  /**
   * Global endpoint for Users
   * @type {string}
   */
  public static readonly USERS_URL = 'users';
  /**
   * Global endpoint for projects
   * @type {string}
   */
  public static readonly PROJECTS_URL = 'projects';
  /**
   * Global endpoint for screens
   * @type {string}
   */
  public static readonly SCREENS_URL = 'screens';
  /**
   * Global endpoint for Widgets
   * @type {string}
   */
  public static readonly WIDGETS_URL = 'widgets';
  /**
   * Global configurations enpoint
   * @type {string}
   */
  public static readonly CONFIGURATIONS_URL = 'configurations';
  /**
   * Global roles endpoint
   * @type {string}
   */
  public static readonly ROLES_URL = 'roles';
}
