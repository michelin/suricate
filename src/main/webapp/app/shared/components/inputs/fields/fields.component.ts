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
import { FormArray, FormGroup } from '@angular/forms';
import { FormField } from '../../../models/frontend/form/form-field';
import { DataTypeEnum } from '../../../enums/data-type.enum';

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
   * @type {FormArray}
   * @public
   */
  @Input()
  public formArray: FormArray;

  /**
   * The list of icons
   * @type {IconEnum}
   * @protected
   */
  protected iconEnum = IconEnum;
  /**
   * The list of material icon codes
   * @type {MaterialIconRecords}
   * @protected
   */
  protected materialIconRecords = MaterialIconRecords;

  /**
   * Contructor
   */
  constructor() {
    super();
  }

  /**
   * Calculate the size of a cell in a row
   */
  protected getInnerFormSize(): number {
    let cellSize = 87;

    if (this.field.fields && this.field.fields.length > 0) {
      const numberOfFieldDisplayed = this.field.fields.filter((field: FormField) => field.type !== DataTypeEnum.HIDDEN);
      cellSize = 87 / numberOfFieldDisplayed.length;
    }

    return cellSize;
  }

  /**
   * Delete a row
   *
   * @param innerFormGroup The form group that reflect the row
   * @param index The index of a the row in the parent form
   */
  protected deleteRow(innerFormGroup: FormGroup, index: number): void {
    this.field.deleteRow.callback(innerFormGroup.value[this.field.deleteRow.attribute]).subscribe(() => {
      this.formArray.removeAt(index);
    });
  }
}
