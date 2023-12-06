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

import { Component, Injector } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { Repository } from '../../shared/models/backend/repository/repository';
import { HttpRepositoryService } from '../../shared/services/backend/http-repository/http-repository.service';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import {
  RepositoryFormFieldsService
} from '../../shared/services/frontend/form-fields/repository-form-fields/repository-form-fields.service';
import { RepositoryRequest } from '../../shared/models/backend/repository/repository-request';
import { RepositoryTypeEnum } from '../../shared/enums/repository-type.enum';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { Observable } from 'rxjs/internal/Observable';
import { BehaviorSubject, EMPTY, forkJoin, of } from 'rxjs';
import { DatePipe } from '@angular/common';

/**
 * Component used to display the list of git repositories
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class RepositoriesComponent extends ListComponent<Repository> {
  /**
   * The repository being built
   */
  private repository: Repository;

  /**
   * Used to disable buttons during repos synchronization
   */
  private disableAllReposSync = new BehaviorSubject<boolean>(false);

  /**
   * Constructor
   *
   * @param httpRepositoryService The HTTP repository service
   * @param repositoryFormFieldsService The repository form fields service
   * @param datePipe The date pipe
   * @param injector The injector
   */
  constructor(
    private readonly httpRepositoryService: HttpRepositoryService,
    private readonly repositoryFormFieldsService: RepositoryFormFieldsService,
    private readonly datePipe: DatePipe,
    protected injector: Injector
  ) {
    super(httpRepositoryService, injector);
    this.initListConfiguration();
    this.initFilter();
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
    return this.translateService.instant('repository.third.label', {
      type: repository.type,
      priority: repository.priority,
      createdDate: this.datePipe.transform(repository.createdDate, 'd MMMM yyyy HH:mm:ss', undefined, this.translateService.currentLang)
    });
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'repository.list',
      actions: [
        {
          icon: IconEnum.SYNCHRONIZE,
          variant: 'miniFab',
          color: 'primary',
          callback: () => this.reloadAllRepositories(),
          tooltip: { message: 'repositories.synchronize.all' },
          hidden: () => !this.objectsPaged.content || this.objectsPaged.content.length === 0,
          disabled: this.disableAllReposSync.asObservable()
        },
        {
          icon: IconEnum.ADD,
          variant: 'miniFab',
          color: 'primary',
          callback: (event: Event) => this.openFormSidenav(event, null, this.addRepository.bind(this)),
          tooltip: { message: 'repository.add' },
          disabled: this.disableAllReposSync.asObservable()
        }
      ]
    };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.dragAndDropDisabled = false;
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.EDIT,
          tooltip: { message: 'repository.edit' },
          color: 'primary',
          variant: 'miniFab',
          callback: (event: Event, repository: Repository) => this.openFormSidenav(event, repository, this.updateRepository.bind(this)),
          disabled: this.disableAllReposSync.asObservable()
        }
      ]
    };
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['priority,createdDate,name,asc'];
  }

  /**
   * {@inheritDoc}
   */
  protected onItemsLoaded() {
    this.initHeaderConfiguration();
    this.dragAndDropDisabled = !this.objectsPaged.content || this.objectsPaged.content.length <= 1;
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param repository The repository clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, repository: Repository, saveCallback: (repositoryRequest: RepositoryRequest) => void): void {
    this.repository = repository ? Object.assign({}, repository) : new Repository();

    this.sidenavService.openFormSidenav({
      title: repository ? 'repository.edit' : 'repository.add',
      formFields: this.repositoryFormFieldsService.generateFormFields(repository),
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
    this.repository[valueChangedEvent.fieldKey] = valueChangedEvent.value;

    if (valueChangedEvent.fieldKey === 'type') {
      return of(this.repositoryFormFieldsService.generateFormFields(this.repository));
    }

    return EMPTY;
  }

  /**
   * Reload all repositories in priority order
   */
  private reloadAllRepositories(): void {
    this.disableAllReposSync.next(true);
    this.dragAndDropDisabled = true;
    this.toastService.sendMessage('repositories.synchronize.running', ToastTypeEnum.INFO);

    this.httpRepositoryService.synchronize().subscribe({
      next: () => {
        this.disableAllReposSync.next(false);
        this.dragAndDropDisabled = false;
        this.toastService.sendMessage('repositories.synchronize.success', ToastTypeEnum.SUCCESS);
      },
      error: () => {
        this.disableAllReposSync.next(false);
        this.dragAndDropDisabled = false;
        this.toastService.sendMessage('repositories.synchronize.failure', ToastTypeEnum.DANGER);
      }
    });
  }

  /**
   * Update a repository
   * If the repository is enabled, all the repositories will be resynchronized in priority order
   * @param repositoryRequest The new repository with the modification made on the form
   */
  private updateRepository(repositoryRequest: RepositoryRequest): void {
    if (repositoryRequest.login.trim().length === 0) {
      repositoryRequest.login = null;
    }

    if (repositoryRequest.password.trim().length === 0) {
      repositoryRequest.password = null;
    }

    this.dragAndDropDisabled = true;
    if (repositoryRequest.enabled) {
      this.toastService.sendMessage('repository.update.and.synchronize.running', ToastTypeEnum.INFO);
    } else {
      this.toastService.sendMessage('repository.update.running', ToastTypeEnum.INFO);
    }

    this.httpRepositoryService.update(this.repository.id, repositoryRequest).subscribe({
      next: () => this.onUpdateCreateSuccess(repositoryRequest),
      error: () => this.onUpdateCreateError(repositoryRequest)
    });
  }

  /**
   * Function used to add a repository
   * @param repositoryRequest The new repository to add with the modification made on the form
   */
  private addRepository(repositoryRequest: RepositoryRequest): void {
    this.disableAllReposSync.next(true);
    this.dragAndDropDisabled = true;
    if (repositoryRequest.enabled) {
      this.toastService.sendMessage('repository.update.and.synchronize.running', ToastTypeEnum.INFO);
    } else {
      this.toastService.sendMessage('repository.update.running', ToastTypeEnum.INFO);
    }

    this.httpRepositoryService.create(repositoryRequest).subscribe({
      next: () => this.onUpdateCreateSuccess(repositoryRequest),
      error: () => this.onUpdateCreateError(repositoryRequest)
    });
  }

  /**
   * Call when a repository has been successfully created/updated
   * @param repositoryRequest The repository request
   */
  private onUpdateCreateSuccess(repositoryRequest: RepositoryRequest): void {
    this.disableAllReposSync.next(false);
    this.dragAndDropDisabled = false;
    if (repositoryRequest.enabled) {
      this.toastService.sendMessage('repository.update.and.synchronize.success', ToastTypeEnum.SUCCESS);
    } else {
      this.toastService.sendMessage('repository.update.success', ToastTypeEnum.SUCCESS);
    }
    super.refreshList();
  }

  /**
   * Call when a repository creation/update failed
   * @param repositoryRequest The repository request
   */
  private onUpdateCreateError(repositoryRequest: RepositoryRequest): void {
    this.disableAllReposSync.next(false);
    this.dragAndDropDisabled = false;
    if (repositoryRequest.enabled) {
      this.toastService.sendMessage('repository.update.and.synchronize.failure', ToastTypeEnum.DANGER);
    } else {
      this.toastService.sendMessage('repository.update.failure', ToastTypeEnum.DANGER);
    }
    super.refreshList();
  }

  /**
   * Update repositories priority when dropped and save them
   */
  public drop(): void {
    this.objectsPaged.content.forEach(repository => {
      repository.priority = this.objectsPaged.content.indexOf(repository) + 1;
    });

    this.disableAllReposSync.next(true);
    this.dragAndDropDisabled = true;
    this.toastService.sendMessage('repository.priority.running', ToastTypeEnum.INFO);

    forkJoin(
      this.objectsPaged.content.map(repository => this.httpRepositoryService.update(repository.id, Object.assign({}, repository), true))
    ).subscribe({
      next: () => {
        this.disableAllReposSync.next(false);
        this.dragAndDropDisabled = false;
        this.toastService.sendMessage('repository.priority.success', ToastTypeEnum.SUCCESS);
      },
      error: () => {
        this.disableAllReposSync.next(false);
        this.dragAndDropDisabled = false;
        this.toastService.sendMessage('repository.priority.failure', ToastTypeEnum.DANGER);
      }
    });
  }
}
