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
import {WSUpdateEvent} from '../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../shared/model/websocket/enums/WSUpdateType';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import * as Stomp from '@stomp/stompjs';
import {SettingsService} from '../../../shared/services/settings.service';

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
   * @type {boolean}
   * @private
   */
  private _isAlive = true;
  /**
   * The screen subscription (Code View)
   * @type {Subscription}
   * @private
   */
  private _screenSubscription: Subscription;

  /**
   * The project as observable
   * @type {Observable<Project>}
   */
  project$: Observable<Project>;

  /**
   * The screen code to display
   * @type {number}
   */
  screenCode: number;

  /**
   * The constructor
   *
   * @param {SidenavService} _sidenavService The sidenav service to inject
   * @param {DashboardService} _dashboardService The dashboard service to inject
   * @param {WebsocketService} _websocketService The websocket service to inject
   * @param {SettingsService} _themeService The theme service
   * @param {ActivatedRoute} _activatedRoute The activated route service
   * @param {Router} _router The router service
   */
  constructor(private _sidenavService: SidenavService,
              private _dashboardService: DashboardService,
              private _websocketService: WebsocketService,
              private _themeService: SettingsService,
              private _activatedRoute: ActivatedRoute,
              private _router: Router) {
  }

  /**
   * Init of the component
   */
  ngOnInit() {
    this._themeService.currentTheme = 'dark-theme';
    this._sidenavService.closeSidenav();
    this.screenCode = this._websocketService.getscreenCode();

    this._dashboardService.currentDisplayedDashboard$
        .pipe(takeWhile(() => this._isAlive))
        .subscribe(project => this.project$ = of(project));

    this._activatedRoute.queryParams.subscribe(params => {
      if (params['token']) {
        this._dashboardService.getOneByToken(params['token']).subscribe(project => {
          this._dashboardService.currentDisplayedDashboardValue = project;
        });

      } else {
        this._dashboardService.currentDisplayedDashboardValue = null;
        this.listenForConnection();
      }
    });

  }

  /**
   * When on code view screen we wait for new connection
   */
  listenForConnection() {
    this._websocketService.startConnection();
    this._screenSubscription = this
        ._websocketService
        .subscribeToDestination(`/user/${this.screenCode}/queue/connect`)
        .pipe(takeWhile(() => this._isAlive))
        .subscribe((stompMessage: Stomp.Message) => {
          this.handleConnectEvent(JSON.parse(stompMessage.body));
        });
  }

  /**
   * Handle the connection event
   *
   * @param {WSUpdateEvent} updateEvent Update event
   */
  handleConnectEvent(updateEvent: WSUpdateEvent) {
    if (updateEvent.type === WSUpdateType.CONNECT) {
      const project: Project = updateEvent.content;

      if (project) {
        this.unsubscribeListening();
        this._websocketService.disconnect();
        this._router.navigate(['/tv'], {queryParams: {token: project.token}});
      }
    }
  }

  /**
   * Unsubscribe to the listening event
   */
  unsubscribeListening() {
    if (this._screenSubscription) {
      this._screenSubscription.unsubscribe();
    }
  }

  /**
   * When the component is destroyed
   */
  ngOnDestroy() {
    this._isAlive = false;
    this._sidenavService.openSidenav();

    this.unsubscribeListening();
    this._websocketService.disconnect();
  }
}
