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

import { ElementRef } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';

import { SlideToggleButtonConfiguration } from '../button/slide-toggle/slide-toggle-button-configuration';
import { FormField } from '../form/form-field';
import { ValueChangedEvent } from '../form/value-changed-event';

/**
 * Configuration used by the form sidenav
 */
export interface FormSidenavConfiguration {
  /**
   * The title of the sidenav
   */
  title: string;

  /**
   * The fields of the form to display
   */
  formFields: FormField[];

  /**
   * Display a message when there is no field to display
   */
  noFieldsMessage?: string;

  /**
   * The function to call when the form should be sent
   */
  save?: (formGroup: UntypedFormGroup) => void;

  /**
   * Function to call when a value of a field has changed
   */
  onValueChanged?: (valueChangedEvent: ValueChangedEvent) => Observable<FormField[]>;

  /**
   * Used to hide the save action
   */
  hideSaveAction?: boolean;

  /**
   * Configuration of the slide toggle button
   */
  slideToggleButtonConfiguration?: SlideToggleButtonConfiguration;

  /**
   * A reference to a component. Used to take screenshot
   */
  componentRef?: ElementRef;
}
