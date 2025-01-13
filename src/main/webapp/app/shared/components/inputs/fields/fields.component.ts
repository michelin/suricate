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

import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';

import { ButtonColorEnum } from '../../../enums/button-color.enum';
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { IconEnum } from '../../../enums/icon.enum';
import { ButtonConfiguration } from '../../../models/frontend/button/button-configuration';
import { FormField } from '../../../models/frontend/form/form-field';
import { InputComponent } from '../input/input.component';

/**
 * Used to display fields of type Fields
 */
@Component({
  selector: 'suricate-fields',
  templateUrl: './fields.component.html',
  styleUrls: ['./fields.component.scss']
})
export class FieldsComponent extends InputComponent implements OnInit {
  @Input()
  public formArray: UntypedFormArray;
  public deleteRowConfig: ButtonConfiguration<{ formGroup: UntypedFormGroup; index: number }>[];

  /**
   * Constructor
   */
  constructor() {
    super();
  }

  /**
   * Called when the component is init
   */
  public override ngOnInit(): void {
    super.ngOnInit();

    if (this.field.deleteRow) {
      this.initDeleteRowConfiguration();
    }
  }

  /**
   * Calculate the size of a cell in a row
   */
  public getInnerFormSize(): number {
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
   * @param index The index of the row in the parent form
   */
  public deleteRow(innerFormGroup: UntypedFormGroup, index: number): void {
    this.field.deleteRow.callback(innerFormGroup.value[this.field.deleteRow.attribute]).subscribe(() => {
      this.formArray.removeAt(index);
    });
  }

  /**
   * Init delete row configuration
   */
  private initDeleteRowConfiguration(): void {
    this.deleteRowConfig = [
      {
        icon: IconEnum.DELETE,
        color: ButtonColorEnum.WARN,
        variant: 'miniFab',
        callback: (event: Event, object: { formGroup: UntypedFormGroup; index: number }) => {
          this.deleteRow(object.formGroup, object.index);
        }
      }
    ];
  }
}
