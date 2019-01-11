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
import {FormGroup, NgForm} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {WidgetParam} from '../../../../shared/model/api/widget/WidgetParam';
import {DashboardService} from '../../dashboard.service';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../shared/components/toast/toast-objects/ToastType';
import {HttpProjectService} from '../../../../shared/services/api/http-project.service';
import {WidgetVariableType} from '../../../../shared/model/enums/WidgetVariableType';
import {HttpProjectWidgetService} from '../../../../shared/services/api/http-project-widget.service';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {Widget} from '../../../../shared/model/api/widget/Widget';
import {HttpAssetService} from '../../../../shared/services/api/http-asset.service';

@Component({
  selector: 'app-edit-project-widget-dialog',
  templateUrl: './edit-project-widget-dialog.component.html',
  styleUrls: ['./edit-project-widget-dialog.component.css']
})
export class EditProjectWidgetDialogComponent implements OnInit {

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
   * The widget variable type
   * @type {WidgetVariableType}
   */
  widgetVariableType = WidgetVariableType;

  /**
   * Constructor
   *
   * @param data The data give to the dialog
   * @param dialogRef The mat dialog ref
   * @param dashboardService The dashboard service to inject
   * @param httpProjectService The http Project service to inject
   * @param httpProjectWidgetService The project widget service to inject
   * @param toastService The notification service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private dialogRef: MatDialogRef<EditProjectWidgetDialogComponent>,
              private dashboardService: DashboardService,
              private httpProjectService: HttpProjectService,
              private httpProjectWidgetService: HttpProjectWidgetService,
              private httpWidgetService: HttpWidgetService,
              private httpAssetService: HttpAssetService,
              private toastService: ToastService) {
  }

  /**
   * Init of the ocmponent
   */
  ngOnInit() {
    this.httpProjectWidgetService.getOneById(this.data.projectWidgetId).subscribe(projectWidget => {
      this.projectWidget = projectWidget;

      this.httpWidgetService.getOneById(projectWidget.widgetId).subscribe(widget => {
        this.widget = widget;
      });
    });
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
   * Get the upload file as base 64
   *
   * @param event
   * @param {NgForm} formSettings
   * @param {string} inputName
   * @param {string} regexValidator
   */
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


  /**
   * Get the param value inside the backend config for a param
   *
   * @param {string} backendConfig The backend config
   * @param {WidgetParam} param The param to find
   * @returns {string} The corresponding value
   */
  getParamValueByParamName(backendConfig: string, param: WidgetParam): string {
    const paramLines: string[] = backendConfig.split('\n');
    const paramLine = paramLines.find((currentParam: string) => currentParam.startsWith(param.name));
    return paramLine ? paramLine.split(/=(.+)?/)[1] : '';
  }

  /**
   * Edit the widget
   *
   * @param {NgForm} formSettings The form info
   */
  editWidget(formSettings: NgForm): void {
    if (formSettings.valid) {
      const form: FormGroup = formSettings.form;
      let backendConfig = '';

      this.widget.params.forEach(param => {
        backendConfig = `${backendConfig}${param.name}=${form.get(param.name).value}\n`;
      });

      this.projectWidget.backendConfig = backendConfig;
      this.httpProjectWidgetService.updateOneById(this.projectWidget.id, this.projectWidget).subscribe(() => {
        this.toastService.sendMessage('Widget Updated successfully', ToastType.SUCCESS);
      });
      this.dialogRef.close();
    }
  }

}
