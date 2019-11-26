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
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import { RepositoryFormFieldsService } from '../../shared/form-fields/repository-form-fields.service';
import { RepositoryRequest } from '../../shared/models/backend/repository/repository-request';
import { RepositoryTypeEnum } from '../../shared/enums/repository-type.enum';
import { EMPTY, Observable, of } from 'rxjs';

/**
 * Component used to display the list of git repositories
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class RepositoriesComponent extends ListComponent<Repository> {
  /**
   * Reflect the state of the repository in the form sidenav
   * @type {Repository}
   * @private
   */
  private repositoryFormSidenav: Repository;

  /**
   * Constructor
   *
   * @param {HttpRepositoryService} httpRepositoryService Suricate service used to manage the http calls for a repository
   * @param {Injector} injector Angular Service used to manage the injection of services
   */
  constructor(private readonly httpRepositoryService: HttpRepositoryService, protected injector: Injector) {
    super(httpRepositoryService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'repository.list',
      actions: [
        {
          icon: IconEnum.ADD,
          variant: 'miniFab',
          color: 'primary',
          callback: (event: Event) => this.openFormSidenav(event, null, this.addRepository.bind(this)),
          tooltip: { message: 'repository.add' }
        }
      ]
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
          callback: (event: Event, repository: Repository) => this.openFormSidenav(event, repository, this.updateRepository.bind(this))
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
    return repository.type === RepositoryTypeEnum.REMOTE ? repository.url : repository.localPath;
  }

  /**
   * {@inheritDoc}
   */
  protected getThirdLabel(repository: Repository): string {
    return repository.type;
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param repository The repository clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, repository: Repository, saveCallback: (repositoryRequest: RepositoryRequest) => void): void {
    this.repositoryFormSidenav = repository ? Object.assign({}, repository) : new Repository();

    this.sidenavService.openFormSidenav({
      title: repository ? 'repository.edit' : 'repository.add',
      formFields: RepositoryFormFieldsService.generateFormFields(repository),
      save: (repositoryRequest: RepositoryRequest) => saveCallback(repositoryRequest),
      onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onValueChanged(valueChangedEvent)
    });
  }

  /**
   * Manage the value changes
   *
   * @param valueChangedEvent Represent the changes on a form field
   */
  private onValueChanged(valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    this.repositoryFormSidenav[valueChangedEvent.fieldKey] = valueChangedEvent.value;

    if (valueChangedEvent.fieldKey === 'type') {
      return of(RepositoryFormFieldsService.generateFormFields(this.repositoryFormSidenav));
    }

    return EMPTY;
  }

  /**
   * Function used to update a repository
   *
   * @param repositoryRequest The new repository with the modification made on the form
   */
  private updateRepository(repositoryRequest: RepositoryRequest): void {
    this.httpRepositoryService.update(this.repositoryFormSidenav.id, repositoryRequest).subscribe(() => {
      super.refreshList();
    });
  }

  /**
   * Function used to add a repository
   *
   * @param repositoryRequest The new repository to add with the modification made on the form
   */
  private addRepository(repositoryRequest: RepositoryRequest): void {
    this.httpRepositoryService.create(repositoryRequest).subscribe(() => {
      this.refreshList();
    });
  }
}
