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

import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import { ActivatedRoute, Event, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { FormSidenavComponent } from '../../../shared/components/form-sidenav/form-sidenav.component';
import { MenuService } from '../../../shared/services/frontend/menu/menu.service';
import { RouteService } from '../../../shared/services/frontend/route/route.service';
import { MenuComponent } from '../menu/menu.component';

/**
 * Hold the sidenav behavior and the main view
 */
@Component({
  selector: 'suricate-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [MatSidenavContainer, MatSidenav, FormSidenavComponent, MenuComponent, MatSidenavContent, RouterOutlet]
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
  private readonly unsubscribe: Subject<void> = new Subject<void>();

  /**
   * Used to hide or display the menu using activated routes
   */
  public shouldHideMenu = true;

  /**
   * Constructor
   * @param router Angular service used to manage routes
   * @param activatedRoute Angular service used to retrieve the component activated route
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute
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
        const deeperActivatedRoute = RouteService.getDeeperActivatedRoute(this.activatedRoute);

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
