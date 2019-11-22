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

import { Injectable } from '@angular/core';
import { EMPTY, from, Observable, of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { DataTypeEnum } from '../enums/data-type.enum';
import { FormStep } from '../models/frontend/form/form-step';
import { IconEnum } from '../enums/icon.enum';
import { HttpCategoryService } from '../services/backend/http-category.service';
import { map, switchMap, tap, toArray } from 'rxjs/operators';
import { MosaicFormOption } from '../models/frontend/form/mosaic-form-option';
import { Category } from '../models/backend/widget/category';
import { HttpAssetService } from '../services/backend/http-asset.service';
import { FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Widget } from '../models/backend/widget/widget';
import { FormField } from '../models/frontend/form/form-field';
import { HttpWidgetService } from '../services/backend/http-widget.service';
import { WidgetParam } from '../models/backend/widget/widget-param';
import { FormOption } from '../models/frontend/form/form-option';
import { WidgetParamValue } from '../models/backend/widget/widget-param-value';
import { CustomValidators } from 'ng2-validation';

/**
 * Service used to build the steps related to a project widget
 */
@Injectable({ providedIn: 'root' })
export class ProjectWidgetFormStepsService {
  /**
   * Constructor
   *
   * @param translateService Ngx translate service used to manage the translations
   */
  constructor(
    private readonly translateService: TranslateService,
    private readonly httpCategoryService: HttpCategoryService,
    private readonly httpAssetService: HttpAssetService,
    private readonly httpWidgetService: HttpWidgetService
  ) {}

  /**
   * Generation of the form options for widget params
   *
   * @param widgetParam The widget param
   */
  private static getFormOptionsForWidgetParam(widgetParam: WidgetParam): Observable<FormOption[]> {
    let formOptions: FormOption[] = [];

    if (widgetParam.values && widgetParam.values.length > 0) {
      formOptions = widgetParam.values.map((widgetParamValue: WidgetParamValue) => {
        return {
          label: widgetParamValue.value,
          value: widgetParamValue.jsKey
        };
      });
    }

    return of(formOptions);
  }

  /**
   * Get the list of steps for a widget
   */
  public generateGlobalSteps(): Observable<FormStep[]> {
    return of([
      {
        key: 'categoryStep',
        title: 'Select category',
        icon: IconEnum.CATEGORY,
        fields: [
          {
            key: 'categoryId',
            label: 'Select a category',
            type: DataTypeEnum.MOSAIC,
            columnNumber: 4,
            mosaicOptions: () => this.getCategoryMosaicOptions(),
            validators: [Validators.required]
          }
        ]
      },
      {
        key: 'widgetStep',
        title: 'Select Widget',
        icon: IconEnum.WIDGET,
        fields: [
          {
            key: 'widgetId',
            label: 'Select widget',
            type: DataTypeEnum.MOSAIC,
            columnNumber: 4,
            mosaicOptions: (formGroup: FormGroup) => this.getWidgetMosaicOptions(formGroup),
            validators: [Validators.required]
          }
        ]
      },
      {
        key: 'widgetConfigurationStep',
        title: 'Configure Widget',
        icon: IconEnum.WIDGET_CONFIGURATION,
        asyncFields: (formGroup: FormGroup, step: FormStep) => this.getWidgetFields(formGroup, step)
      }
    ]);
  }

  private getCategoryMosaicOptions(): Observable<MosaicFormOption[]> {
    return this.httpCategoryService.getAll().pipe(
      switchMap((categories: Category[]) => {
        return from(categories).pipe(
          map((category: Category) => {
            return {
              value: category.id,
              description: category.name,
              imageUrl: this.httpAssetService.getContentUrl(category.assetToken)
            };
          }),
          toArray()
        );
      })
    );
  }

  private getWidgetMosaicOptions(formGroup: FormGroup): Observable<MosaicFormOption[]> {
    const categoryId = formGroup.root.value['categoryStep']['categoryId'];

    if (categoryId || categoryId === 0) {
      return this.httpCategoryService.getCategoryWidgets(categoryId).pipe(
        switchMap((widgets: Widget[]) => {
          return from(widgets).pipe(
            map((widget: Widget) => {
              return {
                value: widget.id,
                description: widget.description,
                imageUrl: this.httpAssetService.getContentUrl(widget.imageToken)
              };
            }),
            toArray()
          );
        })
      );
    }

    return EMPTY;
  }

  private getWidgetFields(formGroup: FormGroup, step: FormStep): Observable<FormField[]> {
    const widgetId = formGroup.root.value['widgetStep']['widgetId'];

    if (widgetId || widgetId === 0) {
      return this.httpWidgetService.getById(widgetId).pipe(
        tap((widget: Widget) => {
          step.description = widget.description;
          step.imageLink = { link: this.httpAssetService.getContentUrl(widget.imageToken) };
        }),
        map((widget: Widget) => {
          return this.generateProjectWidgetFormFields(widget.params);
        })
      );
    }

    return EMPTY;
  }

  private generateProjectWidgetFormFields(widgetParams: WidgetParam[]): FormField[] {
    const formFields = [];

    widgetParams.forEach((widgetParam: WidgetParam) => {
      const formField: FormField = {
        key: widgetParam.name,
        type: widgetParam.type,
        label: widgetParam.description,
        placeholder: widgetParam.usageExample,
        value: widgetParam.defaultValue,
        options: () => ProjectWidgetFormStepsService.getFormOptionsForWidgetParam(widgetParam),
        validators: this.getValidatorsForWidgetParam(widgetParam)
      };

      if (widgetParam.type === DataTypeEnum.BOOLEAN) {
        formField.value = JSON.parse(formField.value);
      }

      formFields.push(formField);
    });

    return formFields;
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

    if (widgetParam.type === DataTypeEnum.NUMBER) {
      formValidators.push(CustomValidators.digits);
    }

    return formValidators;
  }
}
