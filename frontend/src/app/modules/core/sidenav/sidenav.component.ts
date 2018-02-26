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

import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {User} from '../../../shared/model/dto/user/User';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../../dashboard/dashboard.service';
import {UserService} from '../../user/user.service';
import {AuthenticationService} from '../../authentication/authentication.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit {

  public connectedUser: User;
  public dashboards: Project[];

  constructor(private router: Router,
              private changeDetectorRef: ChangeDetectorRef,
              private dashboardService: DashboardService,
              private userService: UserService,
              private authenticationService: AuthenticationService) { }

  ngOnInit() {
    this.userService
        .getConnectedUser()
        .subscribe(user => {
          this.connectedUser = user;
          this.changeDetectorRef.detectChanges();
        });

    this.dashboardService
        .getAll()
        .subscribe(dashboards => {
          this.dashboards = dashboards;
          this.changeDetectorRef.detectChanges();
        });
  }

  getConnectedUserInitial(): string {
    return this.userService.getUserInitial(this.connectedUser);
  }

  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
