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
import {FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {flatMap, map} from 'rxjs/operators';

import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../shared/components/toast/toast-objects/ToastType';
import {HttpProjectWidgetService} from '../../../../shared/services/api/http-project-widget.service';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {Widget} from '../../../../shared/model/api/widget/Widget';
import {HttpAssetService} from '../../../../shared/services/api/http-asset.service';
import {Configuration} from '../../../../shared/model/api/configuration/Configuration';
import {HttpCategoryService} from '../../../../shared/services/api/http-category.service';
import {WidgetParam} from '../../../../shared/model/api/widget/WidgetParam';
import {DataType} from '../../../../shared/model/enums/DataType';
import {FormField} from '../../../../shared/model/app/form/FormField';
import {FormService} from '../../../../shared/services/app/form.service';
import {FormOption} from '../../../../shared/model/app/form/FormOption';
import {WidgetParamValue} from '../../../../shared/model/api/widget/WidgetParamValue';
import {CustomValidators} from 'ng2-validation';

@Component({
  selector: 'app-edit-project-widget-dialog',
  templateUrl: './edit-project-widget-dialog.component.html',
  styleUrls: ['./edit-project-widget-dialog.component.scss']
})
export class EditProjectWidgetDialogComponent implements OnInit {

  /**
   * The project widget form
   */
  projectWidgetForm: FormGroup;
  /**
   * Object that hold the description of the form
   */
  formFields: FormField[];
  /**
   * The project widget to delete
   * @type {ProjectWidget}
   */
  projectWidget: ProjectWidget;

  /**
   * The related widget
   * @type {Widget}
   */
  widget: Widget;

  /**
   * The category configuration of the widget
   */
  configurations: Configuration[];

  /**
   * The widget params
   */
  widgetParams: WidgetParam[];

  /**
   * The widget variable type
   * @type {DataType}
   */
  dataType = DataType;


  /**
   * Constructor
   *
   * @param data The data give to the dialog
   * @param dialogRef The mat dialog ref
   * @param httpProjectWidgetService The project widget service to inject
   * @param httpWidgetService The http widget service to inject
   * @param httpAssetService The http asset service to inject
   * @param httpCategoryService Manage HTTP calls for the category
   * @param toastService The notification service
   * @param formService The form service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private dialogRef: MatDialogRef<EditProjectWidgetDialogComponent>,
              private httpProjectWidgetService: HttpProjectWidgetService,
              private httpWidgetService: HttpWidgetService,
              private httpAssetService: HttpAssetService,
              private httpCategoryService: HttpCategoryService,
              private toastService: ToastService,
              private formService: FormService) {
  }

  /**
   * Init of the component
   */
  ngOnInit() {
    this.httpProjectWidgetService.getOneById(this.data.projectWidgetId).pipe(
      map((projectWidget: ProjectWidget) => this.projectWidget = projectWidget),
      flatMap(() => this.httpWidgetService.getOneById(this.projectWidget.widgetId)),
      map((widget: Widget) => this.widget = widget),
      flatMap(() => this.httpCategoryService.getCategoryConfigurations(this.widget.category.id)),
      map((configurations: Configuration[]) => this.configurations = configurations)
    ).subscribe(() => {
      this.createParamsToDisplay();
      this.generateProjectWidgetForm();
    });
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

    this.widgetParams = [...this.widget.params, ...this.widgetParams];
  }

  /**
   * Create the form to display
   */
  generateProjectWidgetForm() {
    this.generateFormFields();
    this.projectWidgetForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form descriptors
   */
  generateFormFields() {
    this.formFields = [];

    this.widgetParams.forEach((widgetParam: WidgetParam) => {
      const projectWidgetValue = this.getParamValueByParamName(this.projectWidget.backendConfig, widgetParam.name);
      const formField: FormField = {
        key: widgetParam.name,
        type: widgetParam.type,
        label: widgetParam.description,
        placeholder: widgetParam.usageExample,
        value: projectWidgetValue ? projectWidgetValue : widgetParam.defaultValue,
        options: this.getFormOptionsForWidgetParam(widgetParam),
        validators: this.getValidatorsForWidgetParam(widgetParam)
      };

      if (widgetParam.type === DataType.BOOLEAN) {
        formField.value = JSON.parse(formField.value);
      }

      this.formFields.push(formField);
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
   * The get the string image
   *
   * @param {string} assetToken The image
   * @returns {string} The base64 url
   */
  getImageSrc(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
  }

  /**
   * Get the param value inside the backend config for a param
   *
   * @param {string} config The backend config
   * @param {string} paramName The param name to find
   * @returns {string} The corresponding value
   */
  getParamValueByParamName(config: string, paramName: string): string {
    const paramLines: string[] = config.split('\n');
    const paramLine = paramLines.find((currentParam: string) => currentParam.startsWith(paramName));
    return paramLine ? paramLine.split(/=(.+)?/)[1] : '';
  }

  /**
   * Edit the widget
   */
  editWidget(): void {
    this.formService.validate(this.projectWidgetForm);

    if (this.projectWidgetForm.valid) {
      let backendConfig = '';

      this.widget.params.forEach(param => {
        const value = this.projectWidgetForm.get(param.name).value;
        if (param.type === DataType.BOOLEAN) {
          backendConfig = `${backendConfig}${param.name}=${value}\n`;
        } else {
          backendConfig = value ? `${backendConfig}${param.name}=${value}\n` : backendConfig;
        }
      });

      this.projectWidget.backendConfig = backendConfig;
      this.httpProjectWidgetService.updateOneById(this.projectWidget.id, this.projectWidget).subscribe(() => {
        this.toastService.sendMessage('Widget Updated successfully', ToastType.SUCCESS);
      });
      this.dialogRef.close();
    }
  }

}
