/*
 * Copyright 2012-2021 the original author or authors.
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

import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Event, NavigationEnd, Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';

import { RoutesService } from '../../../shared/services/frontend/route/route.service';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HttpUserService } from '../../../shared/services/backend/http-user/http-user.service';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';

/**
 * Hold the sidenav behavior and the main view
 */
@Component({
  selector: 'suricate-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SidenavComponent implements OnInit, OnDestroy {
  /**
   * Reference on the form sidenav
   */
  @ViewChild('formSidenav')
  public formSidenav: MatSidenav;

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * Used to hide or display the menu using activated routes
   */
  public shouldHideMenu = true;

  /**
   * Constructor
   * @param router Angular service used to manage routes
   * @param activatedRoute Angular service used to retrieve the component activated route
   * @param httpUserService The HTTP user service
   * @param authenticationService The authentication service
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpUserService: HttpUserService,
    private readonly authenticationService: AuthenticationService
  ) {}

  /**
   * Init method
   */
  public ngOnInit(): void {
    this.subscribeToRouteEvents();
  }

  /**
   * Called when the component is destroyed
   * All the subscriptions are closed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Manage route events
   */
  private subscribeToRouteEvents(): void {
    this.router.events.pipe(takeUntil(this.unsubscribe)).subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const deeperActivatedRoute = RoutesService.getDeeperActivatedRoute(this.activatedRoute);

        this.shouldHideMenu = MenuService.shouldHideMenu(deeperActivatedRoute);
      }
    });
  }

  /**
   * Used to open the form sidenav
   */
  public openFormSidenav(): void {
    this.formSidenav.open();
  }

  /**
   * Used to close the form side nav
   */
  public closeFormSidenav(): void {
    this.formSidenav.close();
  }
}
