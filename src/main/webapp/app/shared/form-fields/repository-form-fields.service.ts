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
import { Observable, of } from 'rxjs';
import { FormField } from '../models/frontend/form/form-field';
import { RepositoryTypeEnum } from '../enums/repository-type.enum';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { Repository } from '../models/backend/repository/repository';
import { FormOption } from '../models/frontend/form/form-option';
import { TitleCasePipe } from '@angular/common';

/**
 * Service used to build the form fields related to a repository
 */
@Injectable({ providedIn: 'root' })
export class RepositoryFormFieldsService {
  /**
   * Records used to manage the creation of the form fields related to the repository type
   */
  private static repositoryTypeFormFieldsRecords: Record<RepositoryTypeEnum, (repository: Repository) => FormField[]> = {
    [RepositoryTypeEnum.LOCAL]: (repository: Repository) => RepositoryFormFieldsService.getLocalFormFields(repository),
    [RepositoryTypeEnum.REMOTE]: (repository: Repository) => RepositoryFormFieldsService.getRemoteFormFields(repository)
  };

  /**
   * The constructor
   */
  constructor() {}

  /**
   * Generate the form fields for a repository
   *
   * @param repository The repository used to init the form fields
   */
  public static generateFormFields(repository?: Repository): FormField[] {
    let formFields = RepositoryFormFieldsService.getGeneralFormFields(repository);

    if (repository && repository.type) {
      formFields = [...formFields, ...RepositoryFormFieldsService.repositoryTypeFormFieldsRecords[repository.type](repository)];
    }

    return formFields;
  }

  /**
   * Get the general information of a repository
   *
   * @param repository The repository
   */
  private static getGeneralFormFields(repository: Repository): FormField[] {
    return [
      {
        key: 'enabled',
        label: 'repository.enable',
        type: DataTypeEnum.BOOLEAN,
        value: repository ? repository.enabled : false
      },
      {
        key: 'name',
        label: 'name',
        type: DataTypeEnum.TEXT,
        value: repository ? repository.name : null,
        validators: [Validators.required]
      },
      {
        key: 'type',
        label: 'type',
        type: DataTypeEnum.COMBO,
        options: () => RepositoryFormFieldsService.getRepositoryTypeOptions(),
        value: repository ? repository.type : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the form fields related to the local type
   *
   * @param repository The repository used for the init of the fields
   */
  private static getLocalFormFields(repository: Repository): FormField[] {
    return [
      {
        key: 'localPath',
        label: 'path.local',
        type: DataTypeEnum.TEXT,
        value: repository ? repository.localPath : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the form fields related to the remote type
   *
   * @param repository The repository used for the init of the fields
   */
  private static getRemoteFormFields(repository: Repository): FormField[] {
    return [
      {
        key: 'url',
        label: 'url',
        type: DataTypeEnum.TEXT,
        value: repository ? repository.url : null,
        validators: [Validators.required]
      },
      {
        key: 'branch',
        label: 'branch',
        type: DataTypeEnum.TEXT,
        value: repository ? repository.branch : null,
        validators: [Validators.required]
      },
      {
        key: 'login',
        label: 'login',
        type: DataTypeEnum.TEXT,
        value: repository ? repository.login : null,
        validators: [Validators.required]
      },
      {
        key: 'password',
        label: 'password',
        type: DataTypeEnum.PASSWORD,
        value: repository ? repository.password : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the repository type options for the combobox
   */
  private static getRepositoryTypeOptions(): Observable<FormOption[]> {
    const titleCasePipe = new TitleCasePipe();
    const typeOptions: FormOption[] = [];

    Object.keys(RepositoryTypeEnum).forEach(repositoryType => {
      typeOptions.push({
        label: titleCasePipe.transform(repositoryType),
        value: repositoryType
      });
    });

    return of(typeOptions);
  }
}
