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
import {FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef, MatHorizontalStepper} from '@angular/material';

import {Widget} from '../../../../shared/model/api/widget/Widget';
import {DashboardService} from '../../../../modules/dashboard/dashboard.service';
import {HttpCategoryService} from '../../../../shared/services/api/http-category.service';
import {HttpAssetService} from '../../../../shared/services/api/http-asset.service';
import {Category} from '../../../../shared/model/api/widget/Category';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {WidgetAvailabilityEnum} from '../../../../shared/model/enums/WidgetAvailabilityEnum';
import {ProjectWidgetRequest} from '../../../../shared/model/api/ProjectWidget/ProjectWidgetRequest';
import {WidgetParam} from '../../../../shared/model/api/widget/WidgetParam';
import {Configuration} from '../../../../shared/model/api/configuration/Configuration';
import {DataType} from '../../../../shared/model/enums/DataType';
import {FormField} from '../../../../shared/model/app/form/FormField';
import {FormOption} from '../../../../shared/model/app/form/FormOption';
import {WidgetParamValue} from '../../../../shared/model/api/widget/WidgetParamValue';
import {FormService} from '../../../../shared/services/app/form.service';
import {CustomValidators} from 'ng2-validation';

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
   * The form used to add a project widget
   */
  projectWidgetForm: FormGroup;
  /**
   * Describe the project widget form
   */
  projectWidgetFormFields: FormField[];
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
   * @type {DataType}
   */
  dataType = DataType;
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
   * @param {FormService} formService The form service to inject
   * @param {MatDialogRef<AddWidgetDialogComponent>} addWidgetDialogRef The add widget dialog ref
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private httpAssetService: HttpAssetService,
              private httpCategoryService: HttpCategoryService,
              private httpProjectService: HttpProjectService,
              private dashboardService: DashboardService,
              private formService: FormService,
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
      this.generateProjectWidgetFormFields();
      this.projectWidgetForm = this.formService.generateFormGroupForFields(this.projectWidgetFormFields);
    });
    this.step2Completed = true;
    this.changeDetectorRef.detectChanges();
    this.widgetStepper.next();
  }

  /**
   * Create the list of params to display
   */
  createParamsToDisplay() {
    this.widgetParams = [];

    if (this.configurations) {
      this.configurations.forEach(configuration => {
        this.widgetParams.push({
          name: configuration.key,
          description: configuration.key,
          defaultValue: configuration.value,
          type: configuration.dataType,
          required: true
        });
      });
    }

    this.widgetParams = [...this.selectedWidget.params, ...this.widgetParams];
  }

  /**
   * Generate the form descriptors
   */
  generateProjectWidgetFormFields() {
    this.projectWidgetFormFields = [];

    this.widgetParams.forEach((widgetParam: WidgetParam) => {
      const formField: FormField = {
        key: widgetParam.name,
        type: widgetParam.type,
        label: widgetParam.description,
        placeholder: widgetParam.usageExample,
        value: widgetParam.defaultValue,
        options: this.getFormOptionsForWidgetParam(widgetParam),
        validators: this.getValidatorsForWidgetParam(widgetParam)
      };

      if (widgetParam.type === DataType.BOOLEAN) {
        formField.value = JSON.parse(formField.value);
      }

      this.projectWidgetFormFields.push(formField);
    });
  }

  /**
   * Generation of the form options for widget params
   *
   * @param widgetParam The widget param
   */
  getFormOptionsForWidgetParam(widgetParam: WidgetParam): FormOption[] {
    let formOptions: FormOption[] = [];

    if (widgetParam.values) {
      formOptions = widgetParam.values.map((widgetParamValue: WidgetParamValue) => {
        return {
          key: widgetParamValue.jsKey,
          label: widgetParamValue.value
        };
      });
    }

    return formOptions;
  }

  /**
   * Generation of the validators for the widget param
   *
   * @param widgetParam The widget param
   */
  getValidatorsForWidgetParam(widgetParam: WidgetParam): ValidatorFn[] {
    const formValidators: ValidatorFn[] = [];

    if (widgetParam.required) {
      formValidators.push(Validators.required);
    }

    if (widgetParam.acceptFileRegex) {
      formValidators.push(Validators.pattern(widgetParam.acceptFileRegex));
    }

    if (widgetParam.type === DataType.NUMBER) {
      formValidators.push(CustomValidators.digits);
    }

    return formValidators;
  }

  /**
   * Add a new widegt
   */
  addWidget() {
    if (this.projectWidgetForm.valid) {
      let backendConfig = '';

      this.selectedWidget.params.forEach(param => {
        const value = this.projectWidgetForm.get(param.name).value;
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


  getImageSrc(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
  }
}
