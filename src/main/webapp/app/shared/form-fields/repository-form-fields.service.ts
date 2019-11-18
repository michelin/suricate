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
import { map } from 'rxjs/operators';
import { DataTypeEnum } from '../enums/data-type.enum';
import { Validators } from '@angular/forms';
import { Repository } from '../models/backend/repository/repository';
import { FormOption } from '../models/frontend/form/form-option';
import { TitleCasePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { SimpleFormField } from '../models/frontend/form/simple-form-field';

/**
 * Service used to build the form fields related to a repository
 */
@Injectable({ providedIn: 'root' })
export class RepositoryFormFieldsService {
  /**
   * Records used to manage the creation of the form fields related to the repository type
   */
  private repositoryTypeFormFieldsRecords: Record<RepositoryTypeEnum, (translations: string[], repository: Repository) => FormField[]> = {
    [RepositoryTypeEnum.LOCAL]: (translations: string[], repository: Repository) => this.getLocalFormFields(translations, repository),
    [RepositoryTypeEnum.REMOTE]: (translations: string[], repository: Repository) => this.getRemoteFormFields(translations, repository)
  };

  /**
   * The constructor
   *
   * @param translateService The translate service
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Generate the form fields for a repository
   *
   * @param repository The repository used to init the form fields
   */
  public generateFormFields(repository?: Repository): Observable<FormField[]> {
    return this.translateService.get(['name', 'repository.enable', 'type', 'url', 'branch', 'login', 'password', 'local.path']).pipe(
      map((translations: string[]) => {
        const repositoryTypeFormFields =
          repository && repository.type ? this.repositoryTypeFormFieldsRecords[repository.type](translations, repository) : [];
        return [...this.getGeneralFormFields(translations, repository), ...repositoryTypeFormFields];
      })
    );
  }

  /**
   * Get the general information of a repository
   *
   * @param translations The translations
   * @param repository The repository
   */
  private getGeneralFormFields(translations: string[], repository: Repository): SimpleFormField[] {
    return [
      {
        key: 'enabled',
        label: translations['repository.enable'],
        type: DataTypeEnum.BOOLEAN,
        value: repository ? repository.enabled : false
      },
      {
        key: 'name',
        label: translations['name'],
        type: DataTypeEnum.TEXT,
        value: repository ? repository.name : null,
        validators: [Validators.required]
      },
      {
        key: 'type',
        label: translations['type'],
        type: DataTypeEnum.COMBO,
        options: () => this.getRepositoryTypeOptions(),
        value: repository ? repository.type : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the form fields related to the local type
   *
   * @param translations The translations
   * @param repository The repository used for the init of the fields
   */
  getLocalFormFields(translations: string[], repository: Repository) {
    return [
      {
        key: 'localPath',
        label: translations['local.path'],
        type: DataTypeEnum.TEXT,
        value: repository ? repository.localPath : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the form fields related to the remote type
   *
   * @param translations The translations
   * @param repository The repository used for the init of the fields
   */
  getRemoteFormFields(translations: string[], repository: Repository) {
    return [
      {
        key: 'url',
        label: translations['url'],
        type: DataTypeEnum.TEXT,
        value: repository ? repository.url : null,
        validators: [Validators.required]
      },
      {
        key: 'branch',
        label: translations['branch'],
        type: DataTypeEnum.TEXT,
        value: repository ? repository.branch : null,
        validators: [Validators.required]
      },
      {
        key: 'login',
        label: translations['login'],
        type: DataTypeEnum.TEXT,
        value: repository ? repository.login : null,
        validators: [Validators.required]
      },
      {
        key: 'password',
        label: translations['password'],
        type: DataTypeEnum.PASSWORD,
        value: repository ? repository.password : null,
        validators: [Validators.required]
      }
    ];
  }

  /**
   * Get the repository type options for the combobox
   */
  private getRepositoryTypeOptions(): Observable<FormOption[]> {
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
