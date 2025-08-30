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

import { inject, Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Repository } from '../models/backend/repository/repository';
import { HttpFilterService } from '../services/backend/http-filter/http-filter.service';
import { HttpRepositoryService } from '../services/backend/http-repository/http-repository.service';

@Injectable({
	providedIn: 'root'
})
export class CustomAsyncValidatorService {
	private readonly httpRepositoryService = inject(HttpRepositoryService);

	/**
	 * Check repository priority uniqueness
	 * @param currentRepository The current repository
	 */
	public validateRepositoryUniquePriority(currentRepository: Repository): AsyncValidatorFn {
		return (control: AbstractControl): Observable<ValidationErrors> => {
			return this.httpRepositoryService.getAll(HttpFilterService.getInfiniteFilter()).pipe(
				map((repositories) => {
					return repositories.content
						.filter((repository) => repository.id !== currentRepository.id)
						.filter((repository) => repository.priority === control.value).length >= 1
						? { uniquePriority: true }
						: null;
				})
			);
		};
	}
}
