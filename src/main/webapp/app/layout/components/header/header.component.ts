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

import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';

/**
 * The page header component
 */
@Component({
  selector: 'suricate-pages-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  /**
   * The configuration of the header
   * @type {HeaderConfiguration}
   */
  @Input()
  configuration: HeaderConfiguration;
  /**
   * The second title
   * @type {string}
   */
  @Input()
  secondTitle: string;

  /**
   * True if the menu should be display on the page
   */
  @Input()
  showMenu = true;

  /**
   * The page name
   * @type {string}
   */
  pageName: string;

  /**
   * The constructor
   * @param {Router} route The router service
   */
  constructor(private route: Router) {}

  /**
   * When the component is init
   */
  ngOnInit() {
    this.pageName = this.route.url.split('/')[1];
  }
}
