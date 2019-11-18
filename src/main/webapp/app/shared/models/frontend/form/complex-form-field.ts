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

import { FormField } from './form-field';
import { SimpleFormField } from './simple-form-field';
import { Observable } from 'rxjs';

/**
 * Used for inner form fields (in Form Array)
 */
export class ComplexFormField extends FormField {
  /**
   * The list of fields in case of a (FIELDS data type)
   */
  fields: SimpleFormField[];
  /**
   * The list of values to used in the array
   */
  values?: Observable<unknown[]>;
  /**
   * Function used to delete a row
   */
  deleteRow?: { attribute: string; callback: (object: unknown) => Observable<unknown> };
}
