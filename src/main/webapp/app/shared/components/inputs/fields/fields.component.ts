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

import { Component, Injector, Input, OnInit } from '@angular/core';
import { InputComponent } from '../input/input.component';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { FormField } from '../../../models/frontend/form/form-field';
import { IconEnum } from '../../../enums/icon.enum';
import { ButtonConfiguration } from '../../../models/frontend/button/button-configuration';

/**
 * Used to display fields of type Fields
 */
@Component({
  selector: 'suricate-fields',
  templateUrl: './fields.component.html',
  styleUrls: ['./fields.component.scss']
})
export class FieldsComponent extends InputComponent implements OnInit{
  @Input()
  public formArray: UntypedFormArray;
  public deleteRowConfig: ButtonConfiguration<any>[];

  /**
   * Constructor
   *
   * @param injector Manage services injection
   */
  constructor(protected injector: Injector) {
    super(injector);
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    super.ngOnInit()

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
   * @param index The index of a the row in the parent form
   */
  public deleteRow(innerFormGroup: UntypedFormGroup, index: number): void {
    this.field.deleteRow.callback(innerFormGroup.value[this.field.deleteRow.attribute]).subscribe(() => {
      this.formArray.removeAt(index);
    });
  }

  private initDeleteRowConfiguration(): void {
    this.deleteRowConfig = [{
        icon: IconEnum.DELETE,
        color: 'warn',
        variant: 'miniFab',
        callback: (event: Event, object: { formGroup: UntypedFormGroup; index: number }) => {
          this.deleteRow(object.formGroup, object.index)
        }
    }];
  }
}
