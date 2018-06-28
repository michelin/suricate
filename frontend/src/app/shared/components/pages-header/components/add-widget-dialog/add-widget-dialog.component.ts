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
import {MAT_DIALOG_DATA, MatDialogRef, MatHorizontalStepper} from '@angular/material';
import {Category} from '../../../../model/dto/Category';
import {WidgetService} from '../../../../../modules/widget/widget.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {Asset} from '../../../../model/dto/Asset';
import {Widget} from '../../../../model/dto/Widget';
import {WidgetVariableType} from '../../../../model/dto/enums/WidgetVariableType';
import {FormGroup, NgForm} from '@angular/forms';
import {DashboardService} from '../../../../../modules/dashboard/dashboard.service';
import {ProjectWidget} from '../../../../model/dto/ProjectWidget';
import {WidgetAvailabilityEnum} from '../../../../model/dto/enums/WidgetAvailabilityEnum';

/**
 * Dialog used to add a widget
 */
@Component({
  selector: 'app-add-widget-dialog',
  templateUrl: './add-widget-dialog.component.html',
  styleUrls: ['./add-widget-dialog.component.css']
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
   * The constructor
   *
   * @param _data The data send to the dialog
   * @param {WidgetService} _widgetService The widget service
   * @param {DashboardService} _dashboardService The dashboard service
   * @param {DomSanitizer} _domSanitizer The domSanitizer
   * @param {MatDialogRef<AddWidgetDialogComponent>} _addWidgetDialogRef The add widget dialog ref
   * @param {ChangeDetectorRef} _changeDetectorRef The change detector service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private _data: any,
              private _widgetService: WidgetService,
              private _dashboardService: DashboardService,
              private _domSanitizer: DomSanitizer,
              private _addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>,
              private _changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this._widgetService
        .getCategories()
        .subscribe(categories => {
          this.categories = categories;
        });
  }

  getWidgets(categoryId: number) {
    this._widgetService
        .getWidgetsByCategoryId(categoryId)
        .subscribe(widgets => {
          this.widgets = widgets.filter((widget: Widget) => widget.widgetAvailability === WidgetAvailabilityEnum.ACTIVATED);
          this.step1Completed = true;
          this._changeDetectorRef.detectChanges();
          this.widgetStepper.next();
        });

  }

  setSelectedWidget(selectedWidget: Widget) {
    this.selectedWidget = selectedWidget;
    this.step2Completed = true;
    this._changeDetectorRef.detectChanges();
    this.widgetStepper.next();
  }

  addWidget(formSettings: NgForm) {
    if (formSettings.valid) {
      const form: FormGroup = formSettings.form;
      let backendConfig = '';

      for (const param of this.selectedWidget.widgetParams) {
        backendConfig = `${backendConfig}${param.name}=${form.get(param.name).value}\n`;
      }

      const projectWidget: ProjectWidget = new ProjectWidget();
      projectWidget.backendConfig = backendConfig;
      projectWidget.project = this._dashboardService.currentDisplayedDashboardValue;
      projectWidget.widget = this.selectedWidget;

      this._dashboardService
          .addWidgetToProject(projectWidget)
          .subscribe(data => {
            this._addWidgetDialogRef.close();
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

  getImageSrc(image: Asset): SafeUrl {
    if (image != null) {
      return this._domSanitizer.bypassSecurityTrustUrl(`data:${image.contentType};base64,${image.content}`);
    } else {
      return this._domSanitizer.bypassSecurityTrustUrl(``);
    }

  }
}
