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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

@Component({
  selector: 'suricate-slide-toggle',
  templateUrl: './slide-toggle.component.html',
  styleUrls: ['./slide-toggle.component.scss']
})
export class SlideToggleComponent {
  /**
   * The label of the slide toggle
   */
  @Input()
  public label: string;

  /**
   * Is the toggle button checked or not
   */
  @Input()
  public toggleChecked: boolean;

  /**
   * The event emitter
   */
  @Output()
  slideToggleButtonPressed = new EventEmitter<MatSlideToggleChange>();

  /**
   * Trigger the event emitter when the slide toggle button is pressed
   *
   * @param event The current MatSlideToggleChange event
   */
  public slideToggleChange(event: MatSlideToggleChange): void {
    this.slideToggleButtonPressed.emit(event);
  }
}
