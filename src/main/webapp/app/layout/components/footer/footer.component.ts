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

import { Component } from '@angular/core';
import { EnvironmentService } from '../../../shared/services/frontend/environment/environment.service';

/**
 * The footer of the application
 */
@Component({
  selector: 'suricate-pages-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  /**
   * The app version
   */
  public version = EnvironmentService.appVersion;

  /**
   * The env type
   */
  public environment = EnvironmentService.appEnv;

  /**
   * The current year
   */
  public year = new Date().getFullYear();

  /**
   * The constructor
   */
  constructor() {}
}
