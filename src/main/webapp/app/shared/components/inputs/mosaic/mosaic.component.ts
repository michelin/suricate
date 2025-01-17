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

import { NgClass, NgOptimizedImage } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { MatIcon } from '@angular/material/icon';

import { MosaicFormOption } from '../../../models/frontend/form/mosaic-form-option';
import { SpinnerComponent } from '../../spinner/spinner.component';
import { FormField } from '../../../models/frontend/form/form-field';
import { UntypedFormGroup } from '@angular/forms';
import { ValueChangedEvent, ValueChangedType } from '../../../models/frontend/form/value-changed-event';
import { MaterialIconRecords } from '../../../records/material-icon.record';

/**
 * Component used to display the mosaic input type
 */
@Component({
  selector: 'suricate-mosaic',
  templateUrl: './mosaic.component.html',
  styleUrls: ['./mosaic.component.scss'],
  standalone: true,
  imports: [SpinnerComponent, MatGridList, MatGridTile, NgClass, NgOptimizedImage, MatIcon]
})
export class MosaicComponent implements OnInit {
  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  public field: FormField;

  /**
   * The form created in which we have to create the input
   */
  @Input()
  public formGroup: UntypedFormGroup;

  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  public valueChangeEvent = new EventEmitter<ValueChangedEvent>();

  /**
   * The list of material icon codes
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The options related to the mosaic
   */
  public mosaicOptions: MosaicFormOption[];

  /**
   * The form options that has been selected
   */
  public optionSelected: MosaicFormOption;

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    if (this.field.mosaicOptions) {
      this.field.mosaicOptions(this.formGroup).subscribe((mosaicOptions: MosaicFormOption[]) => {
        this.mosaicOptions = mosaicOptions;
      });
    }
  }

  /**
   * Called when object has been selected on the mosaic
   *
   * @param mosaicOption The option selected
   */
  public selectOption(mosaicOption: MosaicFormOption): void {
    this.optionSelected = mosaicOption;
    this.formGroup.controls[this.field.key].setValue(mosaicOption.value);

    this.valueChangeEvent.emit({
      fieldKey: this.field.key,
      value: this.formGroup.value[this.field.key],
      type: 'mosaicOptionSelected'
    });
  }
}
