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

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import {Repository} from '../../../../../../shared/model/dto/Repository';
import {RepositoryService} from '../repository.service';
import {RepositoryTypeEnum} from '../../../../../../shared/model/dto/enums/RepositoryTypeEnum';
import {FormUtils} from '../../../../../../shared/utils/FormUtils';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';

/**
 * Edit a repository
 */
@Component({
  selector: 'app-repository-edit',
  templateUrl: './repository-edit.component.html',
  styleUrls: ['./repository-edit.component.css']
})
export class RepositoryEditComponent implements OnInit {

  /**
   * The edit form
   * @type {FormGroup}
   */
  repositoryForm: FormGroup;

  /**
   * The repository to edit
   * @type Repository
   */
  repository: Repository;

  /**
   * The list of repository types
   */
  repositoryTypeEnum = RepositoryTypeEnum;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {FormBuilder} formBuilder The form builder service
   * @param {RepositoryService} repositoryService The repository service to inject
   * @param {ToastService} toastService The toast service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private repositoryService: RepositoryService,
              private toastService: ToastService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.repositoryService.getOneById(params['repositoryId']).subscribe(repository => {
        this.repository = repository;
        this.initRepoForm();
      });
    });
  }

  /**
   * Init the edit form
   */
  initRepoForm() {
    this.repositoryForm = this.formBuilder.group({
      name: [this.repository.name, [Validators.required, Validators.pattern(/^[a-zA-Z1-9-_]+$/)]],
      url: this.repository.url,
      branch: this.repository.branch,
      login: this.repository.login,
      password: this.repository.password,
      localPath: this.repository.localPath,
      type: [this.repository.type],
      enabled: this.repository.enabled
    });
  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string): boolean {
    return FormUtils.isFieldInvalid(this.repositoryForm, field);
  }

  /**
   * Reset the form when the repository type as changed
   */
  updateFormValidators() {
    if (this.repositoryForm.get('type').value === RepositoryTypeEnum.REMOTE) {
      this.repositoryForm.get('url').validator = Validators.required;
      this.repositoryForm.get('branch').validator = Validators.required;
      FormUtils.resetValidatorsAndErrorsForField(this.repositoryForm, 'localPath');

    } else {
      FormUtils.resetValidatorsAndErrorsForField(this.repositoryForm, 'url');
      FormUtils.resetValidatorsAndErrorsForField(this.repositoryForm, 'branch');
      this.repositoryForm.get('localPath').validator = Validators.required;
    }
  }

  /**
   * action that save the new repository
   */
  saveRepository() {
    if (this.repositoryForm.valid) {
      this.repositoryService
          .updateOneById(this.repository.id, this.repositoryForm.value)
          .subscribe(() => console.log('ok'));
    }
  }
}
