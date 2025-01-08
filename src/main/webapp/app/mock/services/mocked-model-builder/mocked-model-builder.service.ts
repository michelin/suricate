/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { WidgetStateEnum } from '../../../shared/enums/widget-sate.enum';
import { GridProperties } from '../../../shared/models/backend/project/grid-properties';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project-grid/project-grid';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetPosition } from '../../../shared/models/backend/project-widget/project-widget-position';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { FormStep } from '../../../shared/models/frontend/form/form-step';
import { GridOptions } from '../../../shared/models/frontend/grid/grid-options';
import { WizardConfiguration } from '../../../shared/models/frontend/wizard/wizard-configuration';

@Injectable({
  providedIn: 'root'
})
export class MockedModelBuilderService {
  /**
   * Constructor
   *
   * @param formBuilder The form builder
   */
  constructor(private readonly formBuilder: UntypedFormBuilder) {}

  /**
   * Build a mocked project object for the unit tests
   */
  public buildMockedProject(): Project {
    const gridProperties: GridProperties = {
      maxColumn: 5,
      widgetHeight: 300,
      cssStyle: ''
    };

    const grid: ProjectGrid = {
      id: 1,
      time: 30
    };

    return {
      gridProperties: gridProperties,
      librariesToken: ['Token1', 'Token2'],
      name: 'ProjectName',
      screenshotToken: 'ScreenToken',
      image: {
        content: 'content',
        contentType: 'image/png',
        id: 'id',
        lastUpdateDate: new Date(),
        size: 10
      },
      token: 'Token',
      displayProgressBar: false,
      grids: [grid]
    };
  }

  /**
   * Build a mocked FormField for the unit tests
   *
   * @param type The type of the field to create
   */
  public buildMockedFormField(type: DataTypeEnum): FormField {
    return {
      key: 'Key',
      type: type
    };
  }

  /**
   * Build a mocked FormGroup for the unit tests
   *
   * @param type The type of the field to control
   */
  public buildMockedFormGroup(type: DataTypeEnum): UntypedFormGroup {
    const customField = this.buildMockedFormField(type);

    const formGroup: UntypedFormGroup = this.formBuilder.group({});
    formGroup.addControl(customField.key, new UntypedFormControl(customField.value));

    return formGroup;
  }

  /**
   * Build a mocked FormArray for the unit tests
   *
   * @param type The type of the field to control
   */
  public buildMockedFormArray(type: DataTypeEnum): UntypedFormArray {
    return this.buildMockedFormGroup(type).controls[this.buildMockedFormField(type).key] as UntypedFormArray;
  }

  /**
   * Build a mocked WizardConfiguration for the unit tests
   */
  public buildWizardConfiguration(): WizardConfiguration {
    const formSteps: FormStep[] = [];

    for (let i = 0; i < 3; i++) {
      formSteps.push({
        key: 'Key' + i,
        title: 'Title' + i,
        icon: IconEnum.ADD
      });
    }

    return {
      steps: formSteps
    };
  }

  /**
   * Build a mocked project widget for the unit tests
   */
  public buildMockedProjectWidget(): ProjectWidget {
    const widgetPosition: ProjectWidgetPosition = {
      gridColumn: 1,
      gridRow: 1,
      width: 200,
      height: 200
    };

    return {
      id: 1,
      data: 'Data',
      widgetPosition: widgetPosition,
      customStyle: '',
      instantiateHtml: '',
      backendConfig: '',
      log: '',
      lastExecutionDate: '',
      lastSuccessDate: '',
      globalConfigOverridden: true,
      state: WidgetStateEnum.RUNNING,
      projectToken: 'Token',
      widgetId: 1,
      gridId: 1
    };
  }

  /**
   * Build a mocked gridOptions for the unit tests
   */
  public buildGridOptions(): GridOptions {
    return {
      cols: 5,
      rowHeight: 300,
      gap: 10,
      draggable: true,
      resizable: true,
      compactType: 'vertical'
    };
  }
}
