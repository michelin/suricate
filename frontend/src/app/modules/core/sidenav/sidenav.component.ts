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

import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {User} from '../../../shared/model/dto/user/User';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../../dashboard/dashboard.service';
import {UserService} from '../../user/user.service';
import {takeWhile} from 'rxjs/operators';
import {MatSidenav} from '@angular/material';
import {SidenavService} from './sidenav.service';
import {AuthenticationService} from '../../authentication/authentication.service';

/**
 * Hold the sidenav behavior
 */
@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit, AfterViewInit, OnDestroy {

  /**
   * The html sidenav
   * @type {MatSidenav}
   */
  @ViewChild('sidenav') sidenav: MatSidenav;

  /**
   * Used for close the observable subscription
   * @type {boolean}
   * @private
   */
  private _isAlive = true;

  /**
   * The connected user
   * @type {User}
   */
  connectedUser: User;

  /**
   * True if the user is admin
   * @type {boolean}
   */
  isUserAdmin: boolean;

  /**
   * The list of dashboards
   * @type {Project[]}
   */
  dashboards: Project[];

  /**
   * Constructor
   *
   * @param {Router} _router The router service
   * @param {ChangeDetectorRef} _changeDetectorRef The change detector service
   * @param {DashboardService} _dashboardService The dashboard service
   * @param {UserService} _userService The user service
   * @param {AuthenticationService} _authenticationService The authentication service
   * @param {SidenavService} _sidenavService The sidenav service
   */
  constructor(private _router: Router,
              private _changeDetectorRef: ChangeDetectorRef,
              private _dashboardService: DashboardService,
              private _userService: UserService,
              private _authenticationService: AuthenticationService,
              private _sidenavService: SidenavService) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this._dashboardService.currentDashboardList$
        .pipe(
            takeWhile(() => this._isAlive)
        )
        .subscribe(projects => this.dashboards = this._dashboardService.sortByProjectName(projects));

    this._userService.connectedUser$
        .pipe(
            takeWhile(() => this._isAlive)
        )
        .subscribe(connectedUser => this.connectedUser = connectedUser);

    this._dashboardService.getAllForCurrentUser().subscribe();
    this._userService.getConnectedUser().subscribe();
    this.isUserAdmin = this._userService.isAdmin();
  }

  /**
   * Called when the view has been init
   */
  ngAfterViewInit() {
    this._sidenavService
        .subscribeToSidenavOpenCloseEvent()
        .pipe(takeWhile(() => this._isAlive))
        .subscribe((shouldOpen: boolean) => {
          if (shouldOpen) {
            this.sidenav.open();
          } else {
            this.sidenav.close();
          }
        });
  }

  /**
   * Retrieve the initials of the connected user
   *
   * @returns {string} The initials
   */
  getConnectedUserInitial(): string {
    return this._userService.getUserInitial(this.connectedUser);
  }

  /**
   * Logout the user
   */
  logout(): void {
    this._authenticationService.logout();
    this._router.navigate(['/login']);
  }

  /**
   * Called when the component is destoyed
   * All the subscriptions are closed
   */
  ngOnDestroy() {
    this._isAlive = false;
  }
}
