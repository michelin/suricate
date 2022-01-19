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

import { Component, Injector, OnInit } from '@angular/core';
import { InputComponent } from '../input/input.component';
import { MosaicFormOption } from '../../../models/frontend/form/mosaic-form-option';

/**
 * Component used to display the mosaic input type
 */
@Component({
  selector: 'suricate-mosaic',
  templateUrl: './mosaic.component.html',
  styleUrls: ['./mosaic.component.scss']
})
export class MosaicComponent extends InputComponent implements OnInit {
  /**
   * The options related to the mosaic
   */
  public mosaicOptions: MosaicFormOption[];

  /**
   * The form options that has been selected
   */
  public optionSelected: MosaicFormOption;

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

    this.emitValueChange('mosaicOptionSelected');
  }
}
