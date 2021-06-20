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

import {Injectable} from '@angular/core';
import {Project} from '../../../../models/backend/project/project';
import {DataTypeEnum} from '../../../../enums/data-type.enum';
import {Validators} from '@angular/forms';
import {FormField} from '../../../../models/frontend/form/form-field';
import {IconEnum} from '../../../../enums/icon.enum';
import {EMPTY, Observable, of} from "rxjs";
import {FormOption} from "../../../../models/frontend/form/form-option";
import {TitleCasePipe} from "@angular/common";
import {TranslateService} from "@ngx-translate/core";
import {Rotation} from "../../../../models/backend/rotation/rotation";

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
   * Key for the projects form field
   */
  public static readonly projectsFormFieldKey = 'projects';

  /**
   * Key for the projects form field
   */
  public static readonly rotationSpeedFormFieldKey = 'rotationSpeed';

  /**
   * Constructor
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Get the list of form fields for a rotation
   */
  public generateRotationFormFields(projects: Project[], rotation?: Rotation): FormField[] {
    if (projects.length === 0) {
      return [];
    }

    const fields: FormField[] = [
      {
        key: RotationFormFieldsService.rotationNameFormFieldKey,
        label: this.translateService.instant('rotation.name.form.field'),
        iconPrefix: IconEnum.NAME,
        type: DataTypeEnum.TEXT,
        value: rotation ? rotation.name : null,
        validators: [Validators.required]
      },
      {
        key: RotationFormFieldsService.projectsFormFieldKey,
        label: this.translateService.instant('rotation.dashboards.form.field'),
        iconPrefix: IconEnum.DASHBOARD,
        type: DataTypeEnum.MULTIPLE,
        options: () => RotationFormFieldsService.getProjectsAsOptions(projects),
        value: rotation?.rotationProjects.map(rotationProject => rotationProject.project.token),
        validators: [Validators.required]
      }
    ];

    if (rotation) {
      rotation.rotationProjects.forEach(rotationProject => {
        fields.push({
          key: `${RotationFormFieldsService.rotationSpeedFormFieldKey}-${rotationProject.project.token}`,
          label: `${this.translateService.instant('rotation.dashboards.rotation.speed.form.field')} ${rotationProject.project.name}`,
          iconPrefix: IconEnum.DASHBOARD_ROTATION_SPEED,
          type: DataTypeEnum.NUMBER,
          value: rotationProject.rotationSpeed,
          validators: [Validators.required]
        })
      })
    }

    return fields;
  }

  /**
   * Get the project as options
   */
  private static getProjectsAsOptions(projects: Project[]): Observable<FormOption[]> {
    const titleCasePipe = new TitleCasePipe();
    const projectOptions: FormOption[] = [];

    projects.forEach(project => {
      projectOptions.push({
        label: titleCasePipe.transform(project.name),
        value: project.token
      });
    });

    return of(projectOptions);
  }
}
