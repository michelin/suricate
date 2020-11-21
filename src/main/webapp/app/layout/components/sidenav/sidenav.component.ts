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

import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Event, NavigationEnd, Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';

import { RoutesService } from '../../../shared/services/frontend/route/route.service';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';

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
   * @type {MatSidenav}
   * @public
   */
  @ViewChild('formSidenav')
  public formSidenav: MatSidenav;

  /**
   * Used to close observable subscriptions
   * @type {boolean}
   * @private
   */
  private isAlive = true;

  /**
   * Used to hide or display the menu using activated routes
   * @type {boolean}
   * @protected
   */
  public shouldHideMenu = true;

  /**
   * Constructor
   *
   * @param {Router} router Angular service used to manage routes
   * @param {ActivatedRoute} activatedRoute Angular service used to retrieve the component activated route
   */
  constructor(private readonly router: Router, private readonly activatedRoute: ActivatedRoute) {}

  /**
   * Init objects
   */
  public ngOnInit(): void {
    this.subscribeToRouteEvents();
  }

  /**
   * Manage route events
   */
  private subscribeToRouteEvents(): void {
    this.router.events.subscribe((event: Event) => {
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

  /**
   * Called when the component is destroyed
   * All the subscriptions are closed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
  }
}
