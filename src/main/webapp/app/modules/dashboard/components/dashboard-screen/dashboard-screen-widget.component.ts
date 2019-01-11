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
import {TranslateService} from '@ngx-translate/core';
import {TitleCasePipe} from '@angular/common';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material';
import {HttpProjectWidgetService} from '../../../../shared/services/api/http-project-widget.service';
import {EditProjectWidgetDialogComponent} from '../edit-project-widget-dialog/edit-project-widget-dialog.component';

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

  /**
   * The enumeration that hold the state of a widget (used in HTML)
   */
  WidgetStateEnum = WidgetStateEnum;

  /**
   * Constructor
   *
   * @param matDialog The material dialog
   * @param httpWidgetService The Http widget service
   * @param httpProjectWidgetService The http project widget service
   * @param translateService The translation service
   */
  constructor(private matDialog: MatDialog,
              private httpWidgetService: HttpWidgetService,
              private httpProjectWidgetService: HttpProjectWidgetService,
              private translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.httpWidgetService.getOneById(this.projectWidget.widgetId).subscribe(widget => {
      this.widget = widget;
    });
  }

  /**
   * Delete The project widget
   */
  displayDeleteProjectWidgetDialog(): void {
    this.translateService.get(['widget.delete', 'delete.confirm']).subscribe(translations => {
      const titlecasePipe = new TitleCasePipe();

      this.matDialog.open(ConfirmDialogComponent, {
        data: {
          title: translations['widget.delete'],
          message: `${translations['delete.confirm']} ${titlecasePipe.transform(this.widget.name)}`
        }
      }).afterClosed().subscribe(shouldDeleteProjectWidget => {
        if (shouldDeleteProjectWidget) {
          this.httpProjectWidgetService.deleteOneById(this.projectWidget.id).subscribe();
        }
      });

    });
  }

  displayEditProjectWidgetDialog(): void {
    this.matDialog.open(EditProjectWidgetDialogComponent, {
      minWidth: 700,
      data: {projectWidgetId: this.projectWidget.id}
    });
  }
}
