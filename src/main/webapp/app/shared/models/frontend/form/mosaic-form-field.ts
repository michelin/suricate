/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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
import { Observable } from 'rxjs';
import { MosaicFormOption } from './mosaic-form-option';
import { FormGroup } from '@angular/forms';

/**
 * Used for mosaic form fields (in Mosaic datatype)
 */
export class MosaicFormField extends FormField {
  /**
   * The number of columns to display in the mosaic
   */
  columnNumber = 4;
  /**
   * The mosaic options
   */
  mosaicOptions: (formGroup?: FormGroup) => Observable<MosaicFormOption[]>;
}
