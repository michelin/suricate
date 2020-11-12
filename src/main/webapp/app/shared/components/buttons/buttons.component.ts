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
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { ButtonTypeEnum } from '../../enums/button-type.enum';

/**
 * Component used to generate buttons
 */
@Component({
  selector: 'suricate-buttons',
  templateUrl: './buttons.component.html',
  styleUrls: ['./buttons.component.scss']
})
export class ButtonsComponent<T> {
  /**
   * The list of buttons to display
   * @type {ButtonConfiguration[]}
   * @public
   */
  @Input()
  public configurations: ButtonConfiguration<T>[];
  /**
   * Object to raised with the click event
   * @public
   */
  @Input()
  public object: T;

  /**
   * The different type of buttons
   * @type {ButtonTypeEnum}
   * @protected
   */
  public buttonTypeEnum = ButtonTypeEnum;

  /**
   * Records that store the icons code for an enum
   * @type {MaterialIconRecords}
   * @protected
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   */
  constructor() {}

  /**
   * Used to know if the button should be hidden
   *
   * @param configuration The button configuration related to this button
   */
  public shouldDisplayButton(configuration: ButtonConfiguration<T>): boolean {
    return !configuration.hidden || !configuration.hidden();
  }
}
