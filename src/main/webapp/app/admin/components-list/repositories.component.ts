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

import { Component, Injector } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { Repository } from '../../shared/models/backend/repository/repository';
import { HttpRepositoryService } from '../../shared/services/backend/http-repository.service';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DataTypeEnum } from '../../shared/enums/data-type.enum';
import { Validators } from '@angular/forms';
import { RepositoryTypeEnum } from '../../shared/enums/repository-type.enum';
import { FormOption } from '../../shared/models/frontend/form/form-option';
import { TitleCasePipe } from '@angular/common';

/**
 * Component used to display the list of git repositories
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class RepositoriesComponent extends ListComponent<Repository> {
  /**
   * Constructor
   *
   * @param httpRepositoryService Suricate service used to manage the http calls for a repository
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private httpRepositoryService: HttpRepositoryService, protected injector: Injector) {
    super(httpRepositoryService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'repositories.list'
    };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, repository: Repository) => this.openEditSidenav(event, repository)
        }
      ]
    };
  }

  /**
   * {@inheritDoc}
   */
  protected getFirstLabel(repository: Repository): string {
    return repository.name;
  }

  /**
   * {@inheritDoc}
   */
  protected getSecondLabel(repository: Repository): string {
    return repository.url;
  }

  /**
   * {@inheritDoc}
   */
  protected getThirdLabel(repository: Repository): string {
    return repository.type;
  }

  /**
   * Redirect on the edit page
   *
   * @param event The click event
   * @param repository The repository clicked on the list
   */
  private openEditSidenav(event: Event, repository: Repository): void {
    this.translateService.get(['repository.edit']).subscribe((translations: string[]) => {
      this.getFormFields(repository).subscribe((formFields: FormField[]) => {
        this.sidenavService.openFormSidenav({
          title: translations['repository.edit'],
          formFields: formFields,
          save: () => this.updateRepository()
        });
      });
    });
  }

  /**
   * Build the form fields of the repository
   *
   * @param repository The bean
   */
  private getFormFields(repository?: Repository): Observable<FormField[]> {
    return this.translateService.get(['name', 'repository.enable', 'type', 'url', 'branch', 'login', 'password', 'local.path']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
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
            value: repository ? repository.name : '',
            validators: [Validators.required]
          },
          {
            key: 'type',
            label: translations['type'],
            type: DataTypeEnum.COMBO,
            options: this.getRepositoryTypeOptions(),
            value: repository ? repository.type : RepositoryTypeEnum.REMOTE,
            validators: [Validators.required]
          },
          {
            key: 'url',
            label: translations['url'],
            type: DataTypeEnum.TEXT,
            value: repository ? repository.url : '',
            validators: [Validators.required]
          },
          {
            key: 'branch',
            label: translations['branch'],
            type: DataTypeEnum.TEXT,
            value: repository ? repository.branch : '',
            validators: [Validators.required]
          },
          {
            key: 'login',
            label: translations['login'],
            type: DataTypeEnum.TEXT,
            value: repository ? repository.login : '',
            validators: [Validators.required]
          },
          {
            key: 'password',
            label: translations['password'],
            type: DataTypeEnum.PASSWORD,
            value: repository ? repository.password : '',
            validators: [Validators.required]
          }
        ];

        return formFields;
      })
    );
  }

  /**
   * Get the repository type options for the combobox
   */
  getRepositoryTypeOptions(): FormOption[] {
    const titleCasePipe = new TitleCasePipe();
    const typeOptions: FormOption[] = [];

    Object.keys(RepositoryTypeEnum).forEach(repositoryType => {
      typeOptions.push({
        key: repositoryType,
        label: titleCasePipe.transform(repositoryType)
      });
    });

    return typeOptions;
  }

  /**
   * Function used to update a repository
   */
  private updateRepository(): void {}
}
