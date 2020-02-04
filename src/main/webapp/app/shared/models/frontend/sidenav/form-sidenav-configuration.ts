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

import { FormField } from '../form/form-field';
import { ValueChangedEvent } from '../form/value-changed-event';
import { Observable } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

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
   * The function to call when the form should be sent
   */
  save?: (object: unknown) => void;

  /**
   * Function to call when a value of a field has changed
   */
  onValueChanged?: (valueChangedEvent: ValueChangedEvent) => Observable<FormField[]>;

  /**
   * Used to hide the save action
   */
  hideSaveAction?: boolean;

  /**
   * Used to hide the slide toggle button
   */
  displaySlideToggleButton?: boolean;

  /**
   * Function called when the slide toggle button is pressed
   */
  slideToggleButtonPressed?: (event: MatSlideToggleChange, formGroup: FormGroup, formField: FormField[]) => void;
}
