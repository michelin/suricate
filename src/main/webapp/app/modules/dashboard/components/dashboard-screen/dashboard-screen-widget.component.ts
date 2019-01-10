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


import {Component, Input, OnInit} from '@angular/core';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {Widget} from '../../../../shared/model/api/widget/Widget';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {WidgetStateEnum} from '../../../../shared/model/enums/WidgetSateEnum';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'app-dashboard-screen-widget',
  templateUrl: './dashboard-screen-widget.component.html',
  styleUrls: ['./dashboard-screen-widget.component.css']
})
export class DashboardScreenWidgetComponent implements OnInit {

  /**
   * The projectWidget to display
   * @type {ProjectWidget}
   */
  @Input() projectWidget: ProjectWidget;

  /**
   * Tell if we are on
   */
  @Input() readOnly: boolean;

  /**
   * The widget related to this project widget
   */
  widget: Widget;

  constructor(private httpWidgetService: HttpWidgetService) {
  }

  ngOnInit(): void {
    this.httpWidgetService.getOneById(this.projectWidget.widgetId).subscribe(widget => {
      this.widget = widget;
    });
  }

  /**
   * Get the html/CSS code for the widget
   *
   * @param {ProjectWidget} projectWidget The widget
   * @returns {SafeHtml} The html as SafeHtml
   */
  getHtmlAndCss(): string {
    return `
      <style>
        ${this.widget.cssContent}
        ${this.projectWidget.customStyle ? this.projectWidget.customStyle : ''}
      </style>

      ${this.getWidgetHtml()}
    `;
  }

  /**
   * Get The html for a widget
   *t
   * @returns {string} The related html
   */
  getWidgetHtml(): string {
    let widgetHtml = '';

    if (this.widget.delay > 0 && this.projectWidget.log) {
      if (this.projectWidget.state === WidgetStateEnum.STOPPED) {
        widgetHtml = widgetHtml.concat(`
        <div style="position: relative;
                    background-color: #b41e1ee0;
                    width: 100%;
                    z-index: 15;
                    top: 0;
                    text-align: center;
                    font-size: 0.5em;
                    display: flex;
                    align-items: center;
                    justify-content: center">
          <span>
            <mat-icon class="material-icons" style="color: #cfd2da !important;">warning</mat-icon>
          </span>
          <span style="display: inline-block; color: #cfd2da !important">
            Widget execution error
          </span>
        </div>
      `);
      }

      if (this.projectWidget.state === WidgetStateEnum.WARNING) {
        widgetHtml = widgetHtml.concat(`
        <div style="position: relative;
                    background-color: #e8af00db;
                    width: 100%;
                    z-index: 15;
                    top: 0;
                    text-align: center;
                    font-size: 0.5em;
                    display: flex;
                    align-items: center;
                    justify-content: center">
          <span>
            <mat-icon class="material-icons" style="color: #cfd2da !important;">warning</mat-icon>
          </span>
          <span style="display: inline-block; color: #cfd2da !important">
            Issue with remote server. Retrying ...
          </span>
        </div>
      `);
      }
    }

    widgetHtml = widgetHtml.concat(`
      <div class="grid-stack-item-content">
        ${this.projectWidget.instantiateHtml}
      </div>
    `);

    return widgetHtml;
  }

}
