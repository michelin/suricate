/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';
import { TitleCasePipe } from '@angular/common';

import { Repository } from '../../../shared/models/backend/repository/repository';
import { HttpRepositoryService } from '../../../shared/services/backend/http-repository.service';
import { ToastService } from '../../../shared/services/frontend/toast.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { RepositoryTypeEnum } from '../../../shared/enums/repository-type.enum';
import { FormService } from '../../../shared/services/frontend/form.service';
import { FormStep } from '../../../shared/models/frontend/form/form-step';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { FormOption } from '../../../shared/models/frontend/form/form-option';
import { ValueChangedEvent } from '../../../shared/models/frontend/form/value-changed-event';

/**
 * Edit a repository
 */
@Component({
  selector: 'suricate-repository-add-edit',
  templateUrl: './repository-add-edit.component.html',
  styleUrls: ['./repository-add-edit.component.scss']
})
export class RepositoryAddEditComponent implements OnInit {
  /**
   * The edit form
   * @type {FormGroup}
   */
  repositoryForm: FormGroup;
  /**
   * The description of the form
   */
  formSteps: FormStep[];
  /**
   * The repository to edit
   * @type Repository
   */
  repository: Repository;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {Router} router The router service to inject
   * @param {FormService} formService The form service used to generate form
   * @param {HttpRepositoryService} repositoryService The repository service to inject
   * @param {ToastService} toastService The toast service
   * @param {TranslateService} translateService The service used to translate sentences
   */
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private formService: FormService,
    private repositoryService: HttpRepositoryService,
    private toastService: ToastService,
    private translateService: TranslateService
  ) {}

  /**
   * When the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      if (params['repositoryId']) {
        this.repositoryService.getById(params['repositoryId']).subscribe(repository => {
          this.repository = repository;
          this.initRepoForm();
        });
      } else {
        this.initRepoForm();
      }
    });
  }

  /**
   * Init the edit form
   */
  initRepoForm() {
    this.formSteps = [];

    this.generateStepOne()
      .pipe(
        map((stepOne: FormStep) => (this.formSteps[0] = stepOne)),
        flatMap(() => this.generateStepTwo()),
        map((stepTwo: FormStep) => {
          this.formSteps[1] = stepTwo;
          return stepTwo;
        }),
        flatMap((stepTwo: FormStep) => this.generateStepRepoInformation(stepTwo.fields[0].value)),
        map((stepRepoInfo: FormStep) => (this.formSteps[2] = stepRepoInfo))
      )
      .subscribe(() => {
        this.repositoryForm = this.formService.generateFormGroupForSteps(this.formSteps);
      });
  }

  /**
   * Generate the step one of the form
   * "General information step"
   */
  generateStepOne(): Observable<FormStep> {
    return this.translateService.get(['name', 'repository.enable']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'name',
            label: translations['name'],
            type: DataTypeEnum.TEXT,
            value: this.repository ? this.repository.name : '',
            validators: [Validators.required]
          },
          {
            key: 'enabled',
            label: translations['repository.enable'],
            type: DataTypeEnum.BOOLEAN,
            value: this.repository ? this.repository.enabled : false
          }
        ];

        return { fields: formFields };
      })
    );
  }

  /**
   *  Generate the step one of the form
   */
  generateStepTwo(): Observable<FormStep> {
    return this.translateService.get(['type']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'type',
            label: translations['type'],
            type: DataTypeEnum.COMBO,
            options: this.getRepositoryTypeOptions(),
            value: this.repository ? this.repository.type : RepositoryTypeEnum.REMOTE,
            validators: [Validators.required]
          }
        ];

        return { fields: formFields };
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
   * Manage the change event on form
   *
   * @param changeEvent The change event
   */
  manageInputChangeEvent(changeEvent: ValueChangedEvent) {
    if (changeEvent.fieldKey === 'type') {
      this.generateStepRepoInformation(changeEvent.value).subscribe((newStep: FormStep) => {
        this.repositoryForm = this.formService.switchFormGroupStepByAnotherOne(this.repositoryForm, this.formSteps[2], newStep);
        this.formSteps[2] = newStep;
      });
    }
  }

  /**
   * Generate the step three
   *
   * @param inputValue
   */
  generateStepRepoInformation(inputValue: String): Observable<FormStep> {
    return this.translateService.get(['url', 'branch', 'login', 'password', 'local.path']).pipe(
      map((translations: string) => {
        let formFields: FormField[];

        if (inputValue === RepositoryTypeEnum.REMOTE) {
          formFields = [
            {
              key: 'url',
              label: translations['url'],
              type: DataTypeEnum.TEXT,
              value: this.repository ? this.repository.url : '',
              validators: [Validators.required]
            },
            {
              key: 'branch',
              label: translations['branch'],
              type: DataTypeEnum.TEXT,
              value: this.repository ? this.repository.branch : '',
              validators: [Validators.required]
            },
            {
              key: 'login',
              label: translations['login'],
              type: DataTypeEnum.TEXT,
              value: this.repository ? this.repository.login : '',
              validators: [Validators.required]
            },
            {
              key: 'password',
              label: translations['password'],
              type: DataTypeEnum.PASSWORD,
              value: this.repository ? this.repository.password : '',
              validators: [Validators.required]
            }
          ];
        }

        if (inputValue === RepositoryTypeEnum.LOCAL) {
          formFields = [
            {
              key: 'localPath',
              label: translations['local.path'],
              type: DataTypeEnum.TEXT,
              value: this.repository ? this.repository.localPath : '',
              validators: [Validators.required]
            }
          ];
        }

        return { fields: formFields };
      })
    );
  }

  /**
   * action that save the new repository
   */
  saveRepository() {
    this.formService.validate(this.repositoryForm);

    if (this.repositoryForm.valid) {
      const repositoryToAddEdit: Repository = this.repositoryForm.value;

      if (this.repository) {
        this.repositoryService.update(this.repository.id, repositoryToAddEdit).subscribe(() => {
          this.toastService.sendMessage(`Repository ${repositoryToAddEdit.name} updated successfully`, ToastTypeEnum.SUCCESS);
          this.redirectToRepositoryList();
        });
      } else {
        this.repositoryService.create(repositoryToAddEdit).subscribe((repositoryAdded: Repository) => {
          this.toastService.sendMessage(`Repository ${repositoryAdded.name} added successfully`, ToastTypeEnum.SUCCESS);
          this.redirectToRepositoryList();
        });
      }
    }
  }

  /**
   * Redirect to repository list when adding or edit successfully
   */
  redirectToRepositoryList() {
    this.router.navigate(['/repositories']);
  }
}
