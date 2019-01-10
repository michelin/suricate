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

  constructor(private websocketService: WebsocketService) {
  }

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

  /**
   * Get the oommon css for each widget
   *
   * @returns {SafeHtml} AS safe HTML
   */
  getWidgetCommonCSS(): string {
    return `
      <style>
        .grid-stack .icon-background {
          pointer-events: none;
          position: absolute;
          top: 50%;
          left: 50%;
          width: 100% !important;
          height: auto;
          font-size: 11rem;
          transform: translate(-50%, -50%);
          opacity: 0.05;
        }
        .grid-stack .widget {
          text-align: center;
        }
        .grid-stack .widget .fullwidget {
          position: absolute;
          width: 100%;
          height: 100%;
        }
        .grid-stack .grid-stack-item-content {
          height: 100%;
          width: 100%;
          position: absolute;
          top: 0;
          overflow: hidden;
        }
        .grid-stack .grid-stack-item-content * {
          color: #fff;
        }
        .grid-stack .grid-stack-item-content .link {
          display: block;
          width: 90%;
          margin: auto;
          position: relative;
          top: 5px;
          height: 91%;
        }
        .grid-stack .grid-stack-item-content-inner {
          max-height: 100%;
          position: relative;
          top: 50%;
          transform: translateY(-50%);
        }
        .grid-stack h1,.grid-stack h2,.grid-stack h3,.grid-stack h4,.grid-stack h5,.grid-stack p {
          padding: 0;
          margin: 0;
        }
        .grid-stack h1 {
          font-size: 1.4rem;
          text-transform: uppercase;
          margin-top: 6px;
          margin-bottom: 6%;
        }
        .grid-stack h2 {
          font-size: 3.7rem;
        }
        .grid-stack p {
          font-size: 0.8em;
        }
        .grid-stack .widget .more-info {
          color: rgba(255, 255, 255, 0.5);
          font-size: 0.9rem;
          position: absolute;
          bottom: 17px;
          left: 0;
          right: 0;
        }
        .grid-stack .widget .updated-at {
          color: rgba(0, 0, 0, 0.3);
          font-size: 0.9rem;
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
        }
      </style>
    `;
  }

}