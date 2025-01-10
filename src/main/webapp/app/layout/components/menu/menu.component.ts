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
import { Router } from '@angular/router';

import { SettingsService } from '../../../core/services/settings.service';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';

/**
 * Display the menu on the sidenav
 */
@Component({
  selector: 'suricate-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  /**
   * The user connected
   */
  public readonly connectedUser = AuthenticationService.getConnectedUser();

  /**
   * The menu to display
   */
  public readonly menu = MenuService.buildMenu();

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   * @param router Angular service used to manage routes
   * @param settingsService Frontend service used to manage settings
   */
  constructor(
    private readonly router: Router,
    private readonly settingsService: SettingsService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe();
  }

  /**
   * Get the initials of the connected user
   */
  public getInitials(): string {
    return this.connectedUser.firstname && this.connectedUser.lastname
      ? `${this.connectedUser.firstname.substring(0, 1)}${this.connectedUser.lastname.substring(0, 1)}`
      : '';
  }

  /**
   * Logout the user
   */
  public logout(): void {
    AuthenticationService.logout();
    this.router.navigate(['/login']);
  }
}
