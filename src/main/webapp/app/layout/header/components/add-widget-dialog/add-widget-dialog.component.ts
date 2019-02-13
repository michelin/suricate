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

import {ChangeDetectorRef, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormGroup, NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef, MatHorizontalStepper} from '@angular/material';

import {Widget} from '../../../../shared/model/api/widget/Widget';
import {DashboardService} from '../../../../modules/dashboard/dashboard.service';
import {HttpCategoryService} from '../../../../shared/services/api/http-category.service';
import {HttpAssetService} from '../../../../shared/services/api/http-asset.service';
import {Category} from '../../../../shared/model/api/widget/Category';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {WidgetVariableType} from '../../../../shared/model/enums/WidgetVariableType';
import {WidgetAvailabilityEnum} from '../../../../shared/model/enums/WidgetAvailabilityEnum';
import {ProjectWidgetRequest} from '../../../../shared/model/api/ProjectWidget/ProjectWidgetRequest';
import {WidgetParam} from '../../../../shared/model/api/widget/WidgetParam';
import {Configuration} from '../../../../shared/model/api/configuration/Configuration';

/**
 * Dialog used to add a widget
 */
@Component({
  selector: 'app-add-widget-dialog',
  templateUrl: './add-widget-dialog.component.html',
  styleUrls: ['./add-widget-dialog.component.scss']
})
export class AddWidgetDialogComponent implements OnInit {

  /**
   * Hold the HTML Stepper
   * @type {MatHorizontalStepper}
   */
  @ViewChild('widgetStepper') widgetStepper: MatHorizontalStepper;
  /**
   * True if the step 1 has been completed, false otherwise
   * @type {boolean}
   */
  step1Completed = false;
  /**
   * True if the step 2 has been completed, false otherwise
   * @type {boolean}
   */
  step2Completed = false;

  /**
   * The current project token
   */
  projectToken: string;

  /**
   * The widget param enum
   * @type {WidgetVariableType}
   */
  widgetParamEnum = WidgetVariableType;
  /**
   * The list of categories
   * @type {Category[]}
   */
  categories: Category[];
  /**
   * The list of widgets
   * @type {Widget[]}
   */
  widgets: Widget[];
  /**
   * The selected widget
   * @type {Widget}
   */
  selectedWidget: Widget;
  /**
   * The widget params
   */
  widgetParams: WidgetParam[];
  /**
   * The category configuration
   */
  configurations: Configuration[];

  /**
   * The constructor
   *
   * @param data The data send to the dialog
   * @param {HttpAssetService} httpAssetService The asset service
   * @param {HttpCategoryService} httpCategoryService The http category service
   * @param {HttpProjectService} httpProjectService The http project service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {MatDialogRef<AddWidgetDialogComponent>} addWidgetDialogRef The add widget dialog ref
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private httpAssetService: HttpAssetService,
              private httpCategoryService: HttpCategoryService,
              private httpProjectService: HttpProjectService,
              private dashboardService: DashboardService,
              private addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.httpCategoryService.getAll().subscribe(categories => {
      this.categories = categories;
    });

    this.projectToken = this.data.projectToken;
  }

  getWidgets(categoryId: number) {
    this.httpCategoryService.getCategoryWidgets(categoryId).subscribe(widgets => {
      this.widgets = widgets.filter((widget: Widget) => widget.widgetAvailability === WidgetAvailabilityEnum.ACTIVATED);
      this.step1Completed = true;
      this.changeDetectorRef.detectChanges();
      this.widgetStepper.next();
    });
  }

  setSelectedWidget(selectedWidget: Widget) {
    this.selectedWidget = selectedWidget;
    this.httpCategoryService.getCategoryConfigurations(selectedWidget.category.id).subscribe(configurations => {
      this.configurations = configurations;
      this.createParamsToDisplay();
    });
    this.step2Completed = true;
    this.changeDetectorRef.detectChanges();
    this.widgetStepper.next();
  }

  /**
   * Create the list of params to display
   */
  createParamsToDisplay() {
    this.widgetParams = this.selectedWidget.params;

    if (this.configurations) {
      this.configurations.forEach(configuration => {
        this.widgetParams.push({
          name: configuration.key,
          description: configuration.key,
          defaultValue: configuration.value,
          type: WidgetVariableType[configuration.dataType.toString()],
          required: true
        });
      });
    }
  }

  addWidget(formSettings: NgForm) {
    if (formSettings.valid) {
      const form: FormGroup = formSettings.form;
      let backendConfig = '';

      this.selectedWidget.params.forEach(param => {
        const value = form.get(param.name).value;
        backendConfig = value ? `${backendConfig}${param.name}=${value}\n` : `${backendConfig}`;
      });

      const projectWidgetRequest: ProjectWidgetRequest = {
        backendConfig: backendConfig,
        widgetId: this.selectedWidget.id
      };

      this.httpProjectService.addProjectWidgetToProject(this.projectToken, projectWidgetRequest).subscribe(() => {
        this.dashboardService.refreshProject();
        this.addWidgetDialogRef.close();
      });
    }
  }

  getUploadedFileBase64(event: any, formSettings: NgForm, inputName: string, regexValidator: string) {
    const fileReader = new FileReader();
    const regexValidation = new RegExp(regexValidator, 'g');

    if (event.target.files && event.target.files.length > 0) {
      const file: File = event.target.files[0];

      if (regexValidation.test(file.name)) {
        fileReader.readAsDataURL(file);
        fileReader.onloadend = () => {
          formSettings.form.get(inputName).setValue(fileReader.result);
        };

        document.getElementsByClassName(`file-selection-sentence-${inputName}`)[0].textContent = file.name;
      }
    }
  }

  getImageSrc(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
  }
}
