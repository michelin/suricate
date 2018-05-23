/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {SidenavService} from '../../core/sidenav/sidenav.service';
import {WebsocketService} from '../../../shared/services/websocket.service';
import {takeWhile} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import {Project} from '../../../shared/model/dto/Project';
import {Observable} from 'rxjs/Observable';
import {DashboardService} from '../dashboard.service';
import {WSConfiguration} from '../../../shared/model/websocket/WSConfiguration';
import {Subscription} from 'rxjs/Subscription';
import {WSUpdateEvent} from '../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../shared/model/websocket/enums/WSUpdateType';

/**
 * Dashboard TV Management
 */
@Component({
  selector: 'app-code-view',
  templateUrl: './dashboard-tv.component.html',
  styleUrls: ['./dashboard-tv.component.css']
})
export class DashboardTvComponent implements OnInit, OnDestroy {

  /**
   * Tell if the component is displayed
   *
   * @type {boolean}
   */
  isAlive = true;

  /**
   * The project as observable
   */
  project$: Observable<Project>;

  /**
   * The screen code to display
   */
  screenCode: number;

  /**
   * The screen subscription (Code View)
   */
  screenSubscription: Subscription;

  /**
   * The constructor
   *
   * @param {SidenavService} sidenavService The sidenav service to inject
   * @param {DashboardService} dashboardService The dashboard service to inject
   * @param {WebsocketService} websocketService The websocket service to inject
   */
  constructor(private sidenavService: SidenavService,
              private dashboardService: DashboardService,
              private websocketService: WebsocketService) { }

  /**
   * Init of the component
   */
  ngOnInit() {
    this.sidenavService.closeSidenav();
    this.screenCode = this.websocketService.getscreenCode();

    this.dashboardService
        .currendDashbordSubject
        .pipe(takeWhile(() => this.isAlive))
        .subscribe(project => this.project$ = of(project));

    this.listenForConnection();
  }

  /**
   * When on code view screen we wait for new connection
   */
  listenForConnection() {
    this.screenSubscription = this.websocketService
        .subscribe(
            `/user/${this.screenCode}/queue/connect`,
            this.handleConnectEvent.bind(this)
        );
  }

  /**
   * Handle the connection event
   *
   * @param {WSUpdateEvent} updateEvent Update event
   * @param headers The headers
   */
  handleConnectEvent(updateEvent: WSUpdateEvent, headers: any) {
    if (updateEvent.type === WSUpdateType.CONNECT) {
      const project: Project = updateEvent.content;
      if (project) {
        this.unsubscribeListening();
        this.dashboardService.currendDashbordSubject.next(project);
      }
    }
  }

  /**
   * Unsubscribe to the listening event
   */
  unsubscribeListening() {
    if (this.screenSubscription) {
      this.websocketService.unsubscribe(this.screenSubscription);
    }
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
    this.sidenavService.openSidenav();
    this.unsubscribeListening();
  }
}
