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

import { CdkDrag, CdkDropList } from '@angular/cdk/drag-drop';
import { DatePipe, NgClass, NgOptimizedImage } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, EMPTY, forkJoin, ObservableInput, of } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';

import { HeaderComponent } from '../../layout/components/header/header.component';
import { ButtonsComponent } from '../../shared/components/buttons/buttons.component';
import { InputComponent } from '../../shared/components/inputs/input/input.component';
import { ListComponent } from '../../shared/components/list/list.component';
import { PaginatorComponent } from '../../shared/components/paginator/paginator.component';
import { SpinnerComponent } from '../../shared/components/spinner/spinner.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { RepositoryTypeEnum } from '../../shared/enums/repository-type.enum';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { Repository } from '../../shared/models/backend/repository/repository';
import { RepositoryRequest } from '../../shared/models/backend/repository/repository-request';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import { AbstractHttpService } from '../../shared/services/backend/abstract-http/abstract-http.service';
import { HttpRepositoryService } from '../../shared/services/backend/http-repository/http-repository.service';
import { RepositoryFormFieldsService } from '../../shared/services/frontend/form-fields/repository-form-fields/repository-form-fields.service';

/**
 * Component used to display the list of git repositories
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss'],
  imports: [
    HeaderComponent,
    InputComponent,
    FormsModule,
    ReactiveFormsModule,
    SpinnerComponent,
    CdkDropList,
    CdkDrag,
    NgClass,
    NgOptimizedImage,
    ButtonsComponent,
    PaginatorComponent
  ],
  providers: [{ provide: AbstractHttpService, useClass: HttpRepositoryService }]
})
export class RepositoriesComponent extends ListComponent<Repository, RepositoryRequest> {
  private readonly httpRepositoryService = inject(HttpRepositoryService);
  private readonly repositoryFormFieldsService = inject(RepositoryFormFieldsService);
  private readonly datePipe = inject(DatePipe);

  /**
   * The repository being built
   */
  private repository: Repository;

  /**
   * Used to disable buttons during repos synchronization
   */
  private readonly disableAllReposSync = new BehaviorSubject<boolean>(false);

  /**
   * Constructor
   */
  constructor() {
    super();
    this.initListConfiguration();
    this.initFilter();
  }

  /**
   * {@inheritDoc}
   */
  protected override getFirstLabel(repository: Repository): string {
    return repository.name;
  }

  /**
   * {@inheritDoc}
   */
  protected override getSecondLabel(repository: Repository): string {
    return repository.type === RepositoryTypeEnum.REMOTE ? repository.url : repository.localPath;
  }

  /**
   * {@inheritDoc}
   */
  protected override getThirdLabel(repository: Repository): string {
    return this.translateService.instant('repository.third.label', {
      type: repository.type,
      priority: repository.priority,
      createdDate: this.datePipe.transform(
        repository.createdDate,
        'd MMMM yyyy HH:mm:ss',
        undefined,
        this.translateService.currentLang
      )
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
          callback: () => this.reloadAllRepositories(),
          tooltip: { message: 'repositories.synchronize.all' },
          hidden: () => !this.objectsPaged.content || this.objectsPaged.content.length === 0,
          disabled: this.disableAllReposSync.asObservable()
        },
        {
          icon: IconEnum.ADD,
          variant: 'miniFab',
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
          variant: 'miniFab',
          callback: (event: Event, repository: Repository) =>
            this.openFormSidenav(event, repository, this.updateRepository.bind(this)),
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
  protected override onItemsLoaded() {
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
  private openFormSidenav(
    event: Event,
    repository: Repository,
    saveCallback: (formGroup: UntypedFormGroup) => void
  ): void {
    this.repository = repository ? { ...repository } : new Repository();

    this.sidenavService.openFormSidenav({
      title: repository ? 'repository.edit' : 'repository.add',
      formFields: this.repositoryFormFieldsService.generateFormFields(repository),
      save: (formGroup: UntypedFormGroup) => saveCallback(formGroup),
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
   * @param formGroup The form group
   */
  private updateRepository(formGroup: UntypedFormGroup): void {
    const repositoryRequest: RepositoryRequest = formGroup.value;
    if (repositoryRequest.login?.trim().length === 0) {
      repositoryRequest.login = null;
    }

    if (repositoryRequest.password?.trim().length === 0) {
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
   *
   * @param fromGroup The new repository to add with the modification made on the form
   */
  private addRepository(fromGroup: UntypedFormGroup): void {
    const repositoryRequest: RepositoryRequest = fromGroup.value;
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
  public override drop(): void {
    this.objectsPaged.content.forEach((repository) => {
      repository.priority = this.objectsPaged.content.indexOf(repository) + 1;
    });

    this.disableAllReposSync.next(true);
    this.dragAndDropDisabled = true;
    this.toastService.sendMessage('repository.priority.running', ToastTypeEnum.INFO);

    const repositoryUpdates: ObservableInput<void>[] = this.objectsPaged.content.map((repository) =>
      this.httpRepositoryService.update(repository.id, { ...repository }, true)
    );

    forkJoin(repositoryUpdates).subscribe({
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
