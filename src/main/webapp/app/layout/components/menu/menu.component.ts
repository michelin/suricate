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
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { Router } from '@angular/router';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { HttpSettingService } from '../../../shared/services/backend/http-setting/http-setting.service';
import { SettingsService } from '../../../core/services/settings.service';
import { AuthenticationProvider } from '../../../shared/enums/authentication-provider.enum';

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
   * @param httpSettingService Suricate service used to manage settings
   * @param settingsService Frontend service used to manage settings
   */
  constructor(
    private readonly router: Router,
    private readonly httpSettingService: HttpSettingService,
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

  /**
   * Is the connected user logged in by idp or not ?
   */
  public isConnectedByIdp(): boolean {
    return this.isConnectedWithGithub() || this.isConnectedWithGitlab();
  }

  /**
   * Is the connected user logged in with GitHub
   */
  public isConnectedWithGithub(): boolean {
    return this.connectedUser.mode === AuthenticationProvider.GITHUB;
  }

  /**
   * Is the connected user logged in with GitLab
   */
  public isConnectedWithGitlab(): boolean {
    return this.connectedUser.mode === AuthenticationProvider.GITLAB;
  }
}
