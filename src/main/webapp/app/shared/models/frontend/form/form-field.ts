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

import { AsyncValidatorFn, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs';

import { DataTypeEnum } from '../../../enums/data-type.enum';
import { IconEnum } from '../../../enums/icon.enum';
import { UserProject } from '../../backend/user/user-project';
import { FormOption } from './form-option';
import { MosaicFormOption } from './mosaic-form-option';

/**
 * Describe a field used to manage the form
 */
export class FormField {
  /* ***************************************************************************************************************** */
  /*                        Generic Field                                                                              */
  /* ***************************************************************************************************************** */
  /**
   * The key of the field
   */
  key: string;

  /**
   * The value displayed by the form
   */
  label?: string;

  /**
   * The type of the data to insert
   */
  type: DataTypeEnum;

  /* ***************************************************************************************************************** */
  /*                        Simple Field                                                                               */
  /* ***************************************************************************************************************** */
  /**
   * The placeholder to display if needed
   */
  placeholder?: string;

  /**
   * The value to display
   */
  value?: string | string[] | number | boolean;

  /**
   * If the type contains a list of values to display insert them in this attribute
   */
  options?: (filter?: string) => Observable<FormOption[]>;

  /**
   * The mat-icon name that should be used as prefix
   */
  iconPrefix?: IconEnum;

  /**
   * The tooltip to display above the icon suffix
   */
  iconPrefixTooltip?: string;

  /**
   * The mat-icon name that should be used as suffix
   */
  iconSuffix?: IconEnum;

  /**
   * Hint to display to the user
   */
  hint?: string;

  /**
   * True if the field should be disabled
   */
  disabled?: boolean;

  /**
   * True if the field should be readonly
   */
  readOnly?: boolean;

  /**
   * The list of validators for this field
   */
  validators?: ValidatorFn | ValidatorFn[] | null;

  /**
   * Async validators (require an http call)
   */
  asyncValidators?: AsyncValidatorFn | AsyncValidatorFn[] | null;

  /* ***************************************************************************************************************** */
  /*                        Inner Field                                                                                */
  /* ***************************************************************************************************************** */
  /**
   * The list of fields in case of a (FIELDS data type)
   */
  fields?: FormField[];

  /**
   * The list of values to used in the array
   */
  values?: Observable<UserProject[]>;

  /**
   * Function used to delete a row
   */
  deleteRow?: { attribute: string; callback: (object: number) => Observable<void> };

  /* ***************************************************************************************************************** */
  /*                        Mosaic Field                                                                                */
  /* ***************************************************************************************************************** */
  /**
   * The number of columns to display in the mosaic
   */
  columnNumber?: number;

  /**
   * The mosaic options
   */
  mosaicOptions?: (formGroup?: UntypedFormGroup) => Observable<MosaicFormOption[]>;
}
