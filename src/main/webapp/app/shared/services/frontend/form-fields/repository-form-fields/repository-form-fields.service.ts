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

import { TitleCasePipe } from '@angular/common';
import { inject, Injectable } from '@angular/core';
import { Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';

import { DataType } from '../../../../enums/data-type';
import { Icon } from '../../../../enums/icon';
import { RepositoryType } from '../../../../enums/repository-type';
import { Repository } from '../../../../models/backend/repository/repository';
import { FormField } from '../../../../models/frontend/form/form-field';
import { FormOption } from '../../../../models/frontend/form/form-option';
import { CustomAsyncValidatorService } from '../../../../validators/custom-async-validator.service';
import { CustomValidator } from '../../../../validators/custom-validator';

/**
 * Service used to build the form fields related to a repository
 */
@Injectable({ providedIn: 'root' })
export class RepositoryFormFieldsService {
	private readonly translateService = inject(TranslateService);
	private readonly customAsyncValidatorService = inject(CustomAsyncValidatorService);

	/**
	 * Records used to manage the creation of the form fields related to the repository type
	 */
	private static readonly repositoryTypeFormFieldsRecords: Record<
		RepositoryType,
		(repository: Repository) => FormField[]
	> = {
		[RepositoryType.LOCAL]: (repository: Repository) => RepositoryFormFieldsService.getLocalFormFields(repository),
		[RepositoryType.REMOTE]: (repository: Repository) => RepositoryFormFieldsService.getRemoteFormFields(repository)
	};

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
				iconPrefix: Icon.URL,
				type: DataType.TEXT,
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
				iconPrefix: Icon.URL,
				type: DataType.TEXT,
				value: repository ? repository.url : null,
				validators: [Validators.required]
			},
			{
				key: 'branch',
				label: 'branch',
				iconPrefix: Icon.BRANCH,
				type: DataType.TEXT,
				value: repository ? repository.branch : null,
				validators: [Validators.required]
			},
			{
				key: 'login',
				label: 'login',
				iconPrefix: Icon.USERNAME,
				type: DataType.TEXT,
				value: repository ? repository.login : null
			},
			{
				key: 'password',
				label: 'password',
				iconPrefix: Icon.PASSWORD,
				iconSuffix: Icon.SHOW_PASSWORD,
				type: DataType.PASSWORD,
				value: repository ? repository.password : null
			}
		];
	}

	/**
	 * Get the repository type options for the combobox
	 */
	private static getRepositoryTypeOptions(): Observable<FormOption[]> {
		const titleCasePipe = new TitleCasePipe();
		const typeOptions: FormOption[] = [];

		Object.keys(RepositoryType).forEach((repositoryType) => {
			typeOptions.push({
				label: titleCasePipe.transform(repositoryType),
				value: repositoryType
			});
		});

		return of(typeOptions);
	}

	/**
	 * Generate the form fields for a repository
	 *
	 * @param repository The repository used to init the form fields
	 */
	public generateFormFields(repository?: Repository): FormField[] {
		let formFields = this.getGeneralFormFields(repository);

		if (repository?.type) {
			formFields = [
				...formFields,
				...RepositoryFormFieldsService.repositoryTypeFormFieldsRecords[repository.type](repository)
			];
		}

		return formFields;
	}

	/**
	 * Get the general information of a repository
	 *
	 * @param repository The repository
	 */
	private getGeneralFormFields(repository: Repository): FormField[] {
		return [
			{
				key: 'enabled',
				label: 'repository.enable',
				type: DataType.BOOLEAN,
				value: repository ? repository.enabled : false
			},
			{
				key: 'name',
				label: this.translateService.instant('repository.name.form.field'),
				iconPrefix: Icon.NAME,
				type: DataType.TEXT,
				value: repository ? repository.name : null,
				validators: [Validators.required]
			},
			{
				key: 'type',
				label: this.translateService.instant('repository.type.form.field'),
				iconPrefix: Icon.REPOSITORY_TYPE,
				type: DataType.COMBO,
				options: () => RepositoryFormFieldsService.getRepositoryTypeOptions(),
				value: repository ? repository?.type : null,
				validators: [Validators.required]
			},
			{
				key: 'priority',
				label: this.translateService.instant('repository.priority.form.field'),
				iconPrefix: Icon.REPOSITORY_PRIORITY,
				type: DataType.NUMBER,
				value: repository ? repository?.priority : null,
				validators: [Validators.required, CustomValidator.isDigits, CustomValidator.greaterThan0],
				asyncValidators: [this.customAsyncValidatorService.validateRepositoryUniquePriority(repository)]
			}
		];
	}
}
