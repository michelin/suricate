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

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { FormField } from '../models/frontend/form/form-field';
import { TranslateService } from '@ngx-translate/core';
import { Project } from '../models/backend/project/project';

/**
 * Service used to build the form fields related to a project
 */
@Injectable({ providedIn: 'root' })
export class ProjectFormFieldsService {
  /**
   * Constructor
   *
   * @param translateService Ngx translate service used to manage the translations
   */
  constructor(private readonly translateService: TranslateService) {}

  /**
   * Build the form fields of the project
   *
   * @param project The project used to build the form fields
   */
  generateFormFields(project: Project): Observable<FormField[]> {
    return of(null);
  }
}
