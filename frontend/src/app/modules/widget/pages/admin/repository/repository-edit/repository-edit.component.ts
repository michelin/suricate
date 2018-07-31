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
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {FormBuilder} formBuilder The form builder service
   * @param {RepositoryService} repositoryService The repository service to inject
   */
  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private repositoryService: RepositoryService) {
  }

  /**
   * When the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.repositoryService.getOneByName(params['repositoryName']).subscribe(repository => {
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
      url: [this.repository.url],
      branch: [this.repository.branch],
      login: [this.repository.login],
      password: [this.repository.password],
      localPath: [this.repository.localPath],
      type: [this.repository.type],
      enabled: [this.repository.enabled]
    });
  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.repositoryForm.invalid && (this.repositoryForm.get(field).dirty || this.repositoryForm.get(field).touched);
  }

  /**
   * action that save the new repository
   */
  saveRepository() {

  }
}
