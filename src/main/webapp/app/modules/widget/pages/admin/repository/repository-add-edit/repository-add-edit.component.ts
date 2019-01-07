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

import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import {Repository} from '../../../../../../shared/model/api/Repository/Repository';
import {HttpRepositoryService} from '../../../../../../shared/services/api/http-repository.service';
import {FormUtils} from '../../../../../../shared/utils/FormUtils';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../../../shared/components/toast/toast-objects/ToastType';
import {RepositoryTypeEnum} from '../../../../../../shared/model/enums/RepositoryTypeEnum';

/**
 * Edit a repository
 */
@Component({
  selector: 'app-repository-add-edit',
  templateUrl: './repository-add-edit.component.html',
  styleUrls: ['./repository-add-edit.component.css']
})
export class RepositoryAddEditComponent implements OnInit {

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
   * @param {Router} router The router service to inject
   * @param {FormBuilder} formBuilder The form builder service
   * @param {HttpRepositoryService} repositoryService The repository service to inject
   * @param {ToastService} toastService The toast service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private repositoryService: HttpRepositoryService,
              private toastService: ToastService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      if (params['repositoryId']) {
        this.repositoryService.getOneById(params['repositoryId']).subscribe(repository => {
          this.repository = repository;
          this.initRepoForm();
        });

      } else {
        this.initRepoForm();
      }

      this.changeDetectorRef.detectChanges();
    });
  }

  /**
   * Init the edit form
   */
  initRepoForm() {
    this.repositoryForm = this.formBuilder.group({
      name: [this.repository ? this.repository.name : '', [Validators.required]],
      url: [this.repository ? this.repository.url : ''],
      branch: [this.repository ? this.repository.branch : ''],
      login: [this.repository ? this.repository.login : ''],
      password: [this.repository ? this.repository.password : ''],
      localPath: [this.repository ? this.repository.localPath : ''],
      type: [this.repository ? this.repository.type : ''],
      enabled: [this.repository ? this.repository.enabled : false]
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

    this.changeDetectorRef.detectChanges();
  }

  /**
   * action that save the new repository
   */
  saveRepository() {
    if (this.repositoryForm.valid) {
      const repositoryToAddEdit: Repository = this.repositoryForm.value;

      if (this.repository) {
        this.repositoryService.updateOneById(this.repository.id, repositoryToAddEdit).subscribe(() => {
          this.toastService.sendMessage(`Repository ${repositoryToAddEdit.name} updated successfully`, ToastType.SUCCESS);
          this.redirectToRepositoryList();
        });

      } else {
        this.repositoryService
          .addRepository(repositoryToAddEdit)
          .subscribe((repositoryAdded: Repository) => {
            this.toastService.sendMessage(`Repository ${repositoryAdded.name} added successfully`, ToastType.SUCCESS);
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
