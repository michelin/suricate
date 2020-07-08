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
import { HttpFilterService } from '../services/backend/http-filter.service';
import { Page } from '../models/backend/page';

/**
 * Service used to build the steps related to a project widget
 */
@Injectable({ providedIn: 'root' })
export class ProjectWidgetFormStepsService {
  /**
   * Key used for the step where we select a category
   */
  public static readonly selectCategoryStepKey = 'categoryStep';
  /**
   * Key used for the step where we select a widget
   */
  public static readonly selectWidgetStepKey = 'widgetStep';
  /**
   * Key used for the step where we configure a widget
   */
  public static readonly configureWidgetStepKey = 'widgetConfigurationStep';
  /**
   * Key used to store the widget ID
   */
  public static readonly widgetIdFieldKey = 'widgetId';

  /**
   * Constructor
   *
   * @param httpCategoryService Suricate service used to manage HTTP calls
   * @param httpWidgetService Suricate service used to manage http calls for widget
   */
  constructor(private readonly httpCategoryService: HttpCategoryService, private readonly httpWidgetService: HttpWidgetService) {}

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
        key: ProjectWidgetFormStepsService.selectCategoryStepKey,
        title: 'category.select',
        icon: IconEnum.CATEGORY,
        fields: [
          {
            key: 'categoryId',
            type: DataTypeEnum.MOSAIC,
            columnNumber: 4,
            mosaicOptions: () => this.getCategoryMosaicOptions(),
            validators: [Validators.required]
          }
        ]
      },
      {
        key: ProjectWidgetFormStepsService.selectWidgetStepKey,
        title: 'widget.select',
        icon: IconEnum.WIDGET,
        fields: [
          {
            key: ProjectWidgetFormStepsService.widgetIdFieldKey,
            type: DataTypeEnum.MOSAIC,
            columnNumber: 4,
            mosaicOptions: (formGroup: FormGroup) => this.getWidgetMosaicOptions(formGroup),
            validators: [Validators.required]
          }
        ]
      },
      {
        key: ProjectWidgetFormStepsService.configureWidgetStepKey,
        title: 'widget.configuration',
        icon: IconEnum.WIDGET_CONFIGURATION,
        asyncFields: (formGroup: FormGroup, step: FormStep) => this.getWidgetFields(formGroup, step)
      }
    ]);
  }

  /**
   * Generate the form fields for a project widget
   *
   * @param widgetParams The params of the widget
   * @param widgetConfig The configuration already set
   */
  public generateProjectWidgetFormFields(widgetParams: WidgetParam[], widgetConfig?: string): FormField[] {
    const formFields = [];

    widgetParams.forEach((widgetParam: WidgetParam) => {
      let configValue = null;
      if (widgetConfig) {
        configValue = this.retrieveProjectWidgetValueFromConfig(widgetParam.name, widgetConfig);
      }

      const formField: FormField = {
        key: widgetParam.name,
        type: widgetParam.type,
        label: widgetParam.description,
        placeholder: widgetParam.usageExample,
        value: configValue ? configValue : widgetParam.defaultValue,
        options: () => ProjectWidgetFormStepsService.getFormOptionsForWidgetParam(widgetParam),
        validators: this.getValidatorsForWidgetParam(widgetParam)
      };

      if (widgetParam.type === DataTypeEnum.BOOLEAN) {
        formField.value = JSON.parse(formField.value ? formField.value : false);
      }

      formFields.push(formField);
    });

    return formFields;
  }

  private getCategoryMosaicOptions(): Observable<MosaicFormOption[]> {
    return this.httpCategoryService.getAll(HttpFilterService.getInfiniteFilter()).pipe(
      switchMap((categoriesPaged: Page<Category>) => {
        return from(categoriesPaged.content).pipe(
          map((category: Category) => {
            return {
              value: category.id,
              description: category.name,
              imageUrl: HttpAssetService.getContentUrl(category.assetToken)
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
                imageUrl: HttpAssetService.getContentUrl(widget.imageToken)
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
          step.information = widget.info;
          step.category = widget.category;
          step.imageLink = { link: HttpAssetService.getContentUrl(widget.imageToken) };
        }),
        map((widget: Widget) => {
          return this.generateProjectWidgetFormFields(widget.params);
        })
      );
    }

    return EMPTY;
  }

  /**
   * Generation of the validators for the widget param
   *
   * @param widgetParam The widget param
   */
  private getValidatorsForWidgetParam(widgetParam: WidgetParam): ValidatorFn[] {
    const formValidators: ValidatorFn[] = [];

    if (widgetParam.required) {
      formValidators.push(Validators.required);
    }

    if (widgetParam.acceptFileRegex) {
      formValidators.push(Validators.pattern(widgetParam.acceptFileRegex));
    }

    if (widgetParam.type === DataTypeEnum.NUMBER) {
      formValidators.push(Validators.pattern('^[0-9]*$'));
    }

    return formValidators;
  }

  /**
   * Retrieve and existing value from project widget configs
   *
   * @param key The configuration key
   * @param widgetConfig The list of configurations
   */
  public retrieveProjectWidgetValueFromConfig(key: string, widgetConfig?: string): string {
    let value = null;

    widgetConfig.split('\n').forEach((keyValue: string) => {
      const keyValueSplitted = keyValue.split('=');
      if (keyValueSplitted[0] === key) {
        value = keyValueSplitted[1];
        return;
      }
    });

    return value;
  }
}
