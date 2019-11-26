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
import { AuthenticationService } from '../../../shared/services/frontend/authentication.service';
import { Router } from '@angular/router';
import { MenuService } from '../../../shared/services/frontend/menu.service';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';

/**
 * Display the menu on the sidenav
 */
@Component({
  selector: 'suricate-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {
  /**
   * The user connected
   * @type {User}
   * @protected
   */
  protected readonly connectedUser = AuthenticationService.getConnectedUser();
  /**
   * The menu to display
   * @type {MenuConfiguration}
   * @protected
   */
  protected readonly menu = MenuService.buildMenu();
  /**
   * The list of icons
   * @type {IconEnum}
   * @protected
   */
  protected iconEnum = IconEnum;
  /**
   * The list of material icons
   * @type {MaterialIconRecords}
   * @protected
   */
  protected materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param {Router} router Angular service used to manage routes
   */
  constructor(private readonly router: Router) {}

  /**
   * Get the initials of the connected user
   */
  protected getInitials(): string {
    return this.connectedUser.firstname && this.connectedUser.lastname
      ? `${this.connectedUser.firstname.substring(0, 1)}${this.connectedUser.lastname.substring(0, 1)}`
      : '';
  }

  /**
   * Logout the user
   */
  protected logout(): void {
    AuthenticationService.logout();
    this.router.navigate(['/login']);
  }
}
