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

import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';

/**
 * The footer of the application
 */
@Component({
  selector: 'app-pages-footer',
  templateUrl: './pages-footer.component.html',
  styleUrls: ['./pages-footer.component.css']
})
export class PagesFooterComponent implements OnInit {

  /**
   * The app version
   * @type {string}
   */
  version: string = environment.VERSION;

  /**
   * The env type
   * @type {string}
   */
  env: string = environment.ENVIRONMENT;

  /**
   * The constructor
   */
  constructor() {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
  }

}
