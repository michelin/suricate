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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {SidenavService} from '../../core/sidenav/sidenav.service';
import {WebsocketService} from '../../../shared/services/websocket.service';

@Component({
  selector: 'app-code-view',
  templateUrl: './code-view.component.html',
  styleUrls: ['./code-view.component.css']
})
export class CodeViewComponent implements OnInit, OnDestroy {

  /**
   * The screen code to display
   */
  screenCode: number;

  constructor(private sidenavService: SidenavService,
              private websocketService: WebsocketService) { }

  /**
   * Init of the component
   */
  ngOnInit() {
    this.sidenavService.closeSidenav();
    this.screenCode = this.websocketService.screenCode;
  }

  /**
   * When the component is detroyed
   */
  ngOnDestroy() {
    this.sidenavService.openSidenav();
  }
}
