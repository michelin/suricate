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


import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {Widget} from '../../../../shared/model/api/widget/Widget';
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

  @Input() projectWidget: ProjectWidget;
  @Input() readOnly: boolean;
  @ViewChild('projectWidgetElement') projectWidgetElement;

  widget: Widget;

  constructor(private httpWidgetService: HttpWidgetService) {
  }

  ngOnInit(): void {
    this.httpWidgetService.getOneById(this.projectWidget.widgetId).subscribe((widget: Widget) => {
      this.widget = widget;
    });
  }

  /**
   * Get the html/CSS code for the widget
   *
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
   *
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
      ${this.getActionButtonsHtml()}
      <div class="grid-stack-item-content">
        ${this.projectWidget.instantiateHtml}
      </div>
    `);

    return widgetHtml;
  }

  /**
   * Get the html for the action buttons
   *
   * @returns {string} The html string
   */
  getActionButtonsHtml(): string {
    if (this.readOnly) {
      return '';
    }

    return `
      <button id="delete-${this.projectWidget.id}"
              name="delete-${this.projectWidget.id}"
              class="btn-widget btn-widget-delete"
              role="button"
              data-project-widget-id="${this.projectWidget.id}"
              aria-disabled="false">
        <mat-icon class="material-icons">delete_forever</mat-icon>
      </button>

      <button id="edit-${this.projectWidget.id}"
              name="edit-${this.projectWidget.id}"
              class="btn-widget btn-widget-edit"
              role="button"
              data-project-widget-id="${this.projectWidget.id}"
              aria-disabled="false">
        <mat-icon class="material-icons">edit</mat-icon>
      </button>
    `;
  }

}
