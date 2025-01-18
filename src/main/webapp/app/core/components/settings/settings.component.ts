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
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { TranslatePipe } from '@ngx-translate/core';

import { HeaderComponent } from '../../../layout/components/header/header.component';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { SecuritySettingsComponent } from './security-settings/security-settings.component';
import { UxSettingsComponent } from './ux-settings/ux-settings.component';

@Component({
  selector: 'suricate-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
  imports: [HeaderComponent, MatTabGroup, MatTab, UxSettingsComponent, SecuritySettingsComponent, TranslatePipe]
})
export class SettingsComponent implements OnInit {
  /**
   * Configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initHeaderConfiguration();
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'settings.my'
    };
  }
}
