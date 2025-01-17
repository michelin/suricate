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
import { Component, OnInit } from '@angular/core';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { MatIcon } from '@angular/material/icon';

import { MosaicFormOption } from '../../../models/frontend/form/mosaic-form-option';
import { SpinnerComponent } from '../../spinner/spinner.component';
import { BaseInputComponent } from '../base-input/base-input/base-input.component';

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
export class MosaicComponent extends BaseInputComponent implements OnInit {
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

    this.emitValueChangeEventFromType('mosaicOptionSelected');
  }
}
