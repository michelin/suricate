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


import {Component, Input, OnInit,} from '@angular/core';

import {Project} from '../../../../shared/model/api/project/Project';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {WebsocketService} from '../../../../shared/services/websocket.service';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'app-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  styleUrls: ['./dashboard-screen.component.css']
})
export class DashboardScreenComponent implements OnInit {

  /**
   * The project to display
   * @type {Project}
   */
  @Input() project: Project;
  /**
   * The project widget list
   * @type {ProjectWidget[]}
   */
  @Input() projectWidgets: ProjectWidget[];
  /**
   * Tell if the dashboard should be on readOnly or not
   * @type {boolean}
   */
  @Input() readOnly = true;
  /**
   * The screen code
   * @type {number}
   */
  @Input() screenCode: number;

  /**
   * Tell if we should display the screen code
   * @type {boolean}
   */
  displayScreenCode = false;

  /**
   * The options for the plugin angular2-grid
   */
  gridOptions: {};

  /**
   * The constructor
   *
   * @param websocketService The websocket service
   */
  constructor(private websocketService: WebsocketService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit(): void {
    if (!this.screenCode) {
      this.screenCode = this.websocketService.getscreenCode();
    }

    if (this.project) {
      this.initGridStackOptions(this.project);
    }
  }

  /**
   * Init the options for Grid Stack plugin
   *
   * @param {Project} project The project used for the initialization
   */
  initGridStackOptions(project: Project): void {
    this.gridOptions = {
      'max_cols': project.gridProperties.maxColumn,
      'min_cols': 1,
      'row_height': project.gridProperties.widgetHeight,
      'margins': [4],
      'auto_resize': true
    };
  }

  /**
   * Get the CSS for the grid
   *
   * @param {string} css The css
   * @returns {string} The css as html
   */
  getGridCSS(css: string): string {
    return `
      <style>
        .grid {
          ${css}
        }
      </style>
    `;
  }
}