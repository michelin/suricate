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

import { Component, Input } from '@angular/core';

import { ButtonColorEnum } from '../../enums/button-color.enum';
import { ButtonTypeEnum } from '../../enums/button-type.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { NgTemplateOutlet, NgClass, AsyncPipe } from '@angular/common';
import { MatButton, MatMiniFabButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

/**
 * Component used to generate buttons
 */
@Component({
    selector: 'suricate-buttons',
    templateUrl: './buttons.component.html',
    styleUrls: ['./buttons.component.scss'],
    standalone: true,
    imports: [NgTemplateOutlet, MatButton, MatTooltip, MatIcon, NgClass, MatMiniFabButton, AsyncPipe, TranslatePipe]
})
export class ButtonsComponent<T> {
  /**
   * The list of buttons to display
   */
  @Input()
  public configurations: ButtonConfiguration<T>[];

  /**
   * Object to raised with the click event
   */
  @Input()
  public object: T;

  /**
   * The different type of buttons
   */
  public buttonTypeEnum = ButtonTypeEnum;

  /**
   * The different color of buttons
   */
  public buttonColorEnum = ButtonColorEnum;

  /**
   * Records that store the icons code for an enum
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Used to know if the button should be hidden
   *
   * @param configuration The button configuration related to this button
   * @param object The object related to this button
   */
  public shouldDisplayButton(configuration: ButtonConfiguration<T>, object: T): boolean {
    return !configuration.hidden || !configuration.hidden(object);
  }
}
