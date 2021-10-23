/*
 * Copyright 2012-2021 the original author or authors.
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
import { Project } from '../../../../models/backend/project/project';
import { DataTypeEnum } from '../../../../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { FormField } from '../../../../models/frontend/form/form-field';
import { IconEnum } from '../../../../enums/icon.enum';
import { EMPTY, Observable, of } from 'rxjs';
import { FormOption } from '../../../../models/frontend/form/form-option';
import { TitleCasePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Rotation } from '../../../../models/backend/rotation/rotation';
import { RotationProjectRequest } from '../../../../models/backend/rotation-project/rotation-project-request';
import { CustomValidator } from '../../../../validators/custom-validator';

/**
 * Service used to build the form fields related to a rotation
 */
@Injectable({ providedIn: 'root' })
export class RotationFormFieldsService {
  /**
   * Key for the name form field
   */
  public static readonly rotationNameFormFieldKey = 'name';

  /**
   * Key for the progress bar form field
   */
  public static readonly rotationProgressBarFormFieldKey = 'progressBar';

  /**
   * Key for the project token form field
   */
  public static readonly projectTokenFormFieldKey = 'projectToken';

  /**
   * Key for the projects form field
   */
  public static readonly rotationSpeedFormFieldKey = 'rotationSpeed';

  /**
   * Constructor
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Get the list of form fields for a rotation creation
   */
  public generateRotationFormFields(rotation?: Rotation): FormField[] {
    console.warn(rotation);
    return [
      {
        key: RotationFormFieldsService.rotationNameFormFieldKey,
        label: this.translateService.instant('rotation.name.form.field'),
        iconPrefix: IconEnum.NAME,
        type: DataTypeEnum.TEXT,
        value: rotation ? rotation.name : null,
        validators: [Validators.required]
      },
      {
        key: RotationFormFieldsService.rotationProgressBarFormFieldKey,
        label: this.translateService.instant('rotation.progress.bar.form.field'),
        iconPrefix: IconEnum.PROGRESS_BAR,
        type: DataTypeEnum.BOOLEAN,
        value: rotation ? rotation.progressBar : false
      }
    ];
  }

  /**
   * Get the list of form fields when a dashboard is selected during rotation creation
   *
   * @param project The project selected
   * @param rotationProjectRequest The rotation project request, if the project already has been selected
   */
  public generateDashboardOptionsFormFields(project: Project, rotationProjectRequest?: RotationProjectRequest): FormField[] {
    return [
      {
        key: RotationFormFieldsService.projectTokenFormFieldKey,
        type: DataTypeEnum.HIDDEN,
        value: project.token,
        validators: [Validators.required]
      },
      {
        key: RotationFormFieldsService.rotationSpeedFormFieldKey,
        label: this.translateService.instant('rotation.dashboards.rotation.speed.form.field'),
        iconPrefix: IconEnum.DASHBOARD_ROTATION_SPEED,
        type: DataTypeEnum.NUMBER,
        value: rotationProjectRequest ? rotationProjectRequest.rotationSpeed : null,
        validators: [CustomValidator.greaterThan0IfDefined]
      }
    ];
  }
}
