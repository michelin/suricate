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

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {Category} from '../../../../model/dto/Category';
import {WidgetService} from '../../../../services/widget.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {Asset} from '../../../../model/dto/Asset';
import {Widget} from '../../../../model/dto/Widget';
import {WidgetVariableEnum} from '../../../../model/dto/enums/WidgetVariableEnum';
import {FormGroup, NgForm} from '@angular/forms';
import {ProjectWidget} from '../../../../model/dto/ProjectWidget';
import {DashboardService} from '../../../../services/dashboard.service';
import {HeaderDashboardSharedService} from '../../../../services/header-dashboard-shared.service';

@Component({
  selector: 'app-add-widget-dialog',
  templateUrl: './add-widget-dialog.component.html',
  styleUrls: ['./add-widget-dialog.component.css']
})
export class AddWidgetDialogComponent implements OnInit {

  widgetVariableEnum = WidgetVariableEnum;
  categories: Category[];
  widgets: Widget[];
  selectedWidget: Widget;

  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private widgetService: WidgetService,
              private dashboardService: DashboardService,
              private headerDashboardSharedService: HeaderDashboardSharedService,
              private domSanitizer: DomSanitizer,
              private addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>) { }

  ngOnInit() {
    this.widgetService
        .getCategories()
        .subscribe(categories => {
          this.categories = categories;
        });
  }

  getWidgets(categoryId: number) {
    this.widgetService
        .getWidgetsByCategoryId(categoryId)
        .subscribe(widgets => {
          this.widgets = widgets;
        });
  }

  setSelectedWidget(selectedWidget: Widget) {
    this.selectedWidget = selectedWidget;
  }

  addWidget(formSettings: NgForm) {
    if (formSettings.valid) {
      const form: FormGroup = formSettings.form;
      let backendConfig = '';

      for (const variable of this.selectedWidget.widgetVariables) {
        backendConfig = `${backendConfig}${variable.name}=${form.get(variable.name).value}\n`;
      }

      const projectWidget: ProjectWidget = new ProjectWidget();
      projectWidget.backendConfig = backendConfig;
      projectWidget.projectId = this.data.projectId;
      projectWidget.widgetId = this.selectedWidget.widgetId;

      this.dashboardService
          .addWidgetToProject(projectWidget)
          .subscribe(data => {
            this.headerDashboardSharedService.projectDashboardToDisplay.next(data);
            this.addWidgetDialogRef.close();
          });
    }
  }

  getImageSrc(image: Asset): SafeUrl {
    if (image != null) {
      return this.domSanitizer.bypassSecurityTrustUrl(`data:${image.contentType};base64,${image.content}`);
    } else {
      return this.domSanitizer.bypassSecurityTrustUrl(``);
    }

  }
}
