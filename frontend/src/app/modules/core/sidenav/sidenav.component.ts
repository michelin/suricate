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
import {MatSidenav} from '@angular/material';
import {takeWhile} from 'rxjs/operators';

import {SidenavService} from './sidenav.service';
import {User} from '../../../shared/model/dto/user/User';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../../dashboard/dashboard.service';
import {UserService} from '../../user/user.service';
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
  private isAlive = true;

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
   * @param {Router} router The router service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {UserService} userService The user service
   * @param {AuthenticationService} authenticationService The authentication service
   * @param {SidenavService} sidenavService The sidenav service
   */
  constructor(private router: Router,
              private changeDetectorRef: ChangeDetectorRef,
              private dashboardService: DashboardService,
              private userService: UserService,
              private authenticationService: AuthenticationService,
              private sidenavService: SidenavService) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.dashboardService.currentDashboardList$
        .pipe(
            takeWhile(() => this.isAlive)
        )
        .subscribe(projects => this.dashboards = this.dashboardService.sortByProjectName(projects));

    this.userService.connectedUser$
        .pipe(
            takeWhile(() => this.isAlive)
        )
        .subscribe(connectedUser => this.connectedUser = connectedUser);

    this.dashboardService.getAllForCurrentUser().subscribe();
    this.userService.getConnectedUser().subscribe();
    this.isUserAdmin = this.userService.isAdmin();
  }

  /**
   * Called when the view has been init
   */
  ngAfterViewInit() {
    this.sidenavService
        .subscribeToSidenavOpenCloseEvent()
        .pipe(takeWhile(() => this.isAlive))
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
    return this.userService.getUserInitial(this.connectedUser);
  }

  /**
   * Logout the user
   */
  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  /**
   * Called when the component is destoyed
   * All the subscriptions are closed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }
}
