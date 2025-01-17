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

import { Component } from '@angular/core';
import { MatLabel } from '@angular/material/form-field';
import { TranslatePipe } from '@ngx-translate/core';
import { ColorPickerModule } from 'ngx-color-picker';

import { BaseInputComponent } from '../base-input/base-input/base-input.component';

/**
 * Component used to display the color picker
 */
@Component({
  selector: 'suricate-color-picker',
  templateUrl: './color-picker.component.html',
  styleUrls: ['./color-picker.component.scss'],
  standalone: true,
  imports: [MatLabel, ColorPickerModule, TranslatePipe]
})
export class ColorPickerComponent extends BaseInputComponent {
  /**
   * Notification change when the color as changed
   *
   * @param color The new color
   */
  public colorChanged(color: string): void {
    this.getFormControl().setValue(color);
  }

  /**
   * Get the field value
   */
  public getFieldValue(): string {
    return this.field.value as string;
  }
}
