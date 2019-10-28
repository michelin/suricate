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
   */
  @Input()
  public configurations: ButtonConfiguration<T>[];
  /**
   * Object to raised with the click event
   */
  @Input()
  public object: T;

  /**
   * Records that store the icons code for an enum
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   */
  constructor() {}
}
