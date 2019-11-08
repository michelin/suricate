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
   * @public
   */
  public readonly connectedUser = AuthenticationService.getConnectedUser();
  /**
   * The menu to display
   * @type {User}
   * @public
   */
  public readonly menu = MenuService.buildMenu();

  /**
   * Constructor
   *
   * @param {Router} router Angular service used to manage routing
   */
  constructor(private readonly router: Router) {}

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
