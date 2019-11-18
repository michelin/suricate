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

import { Component, Input } from '@angular/core';
import { InputComponent } from '../input.component';
import { MaterialIconRecords } from '../../../records/material-icon.record';
import { IconEnum } from '../../../enums/icon.enum';
import { FormArray } from '@angular/forms';
import { ComplexFormField } from '../../../models/frontend/form/complex-form-field';

/**
 * Used to display fields of type Fields
 */
@Component({
  selector: 'suricate-fields',
  templateUrl: './fields.component.html',
  styleUrls: ['./fields.component.scss']
})
export class FieldsComponent extends InputComponent {
  /**
   * The form array
   */
  @Input()
  formArray: FormArray;
  /**
   * Object that hold different information used for the instantiation of the inputs
   */
  @Input()
  field: ComplexFormField;

  /**
   * The list of icons
   */
  protected iconEnum = IconEnum;
  /**
   * The list of material icon codes
   */
  protected materialIconRecords = MaterialIconRecords;

  constructor() {
    super();
  }

  ngOnInit() {}

  getInnerFormSize(): number {
    return this.field.fields && this.field.fields.length > 0 ? 87 / this.field.fields.length : 87;
  }

  deleteRow(index: number) {
    this.formArray.removeAt(index);
  }
}
