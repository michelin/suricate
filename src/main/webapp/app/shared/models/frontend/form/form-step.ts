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

import { FormField } from './form-field';
import { IconEnum } from '../../../enums/icon.enum';
import { Observable } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { LinkConfiguration } from '../link/link-configuration';
import { Category } from '../../backend/widget/category';

/**
 * Describe a step of the wizard
 */
export class FormStep {
  /**
   * The key of the step
   */
  key: string;
  /**
   * The title of the step
   */
  title: string;
  /**
   * The icon that should displayed in the step
   */
  icon: IconEnum;
  /**
   * If we want to display an image on the step
   */
  imageLink?: LinkConfiguration;
  /**
   * Description of the step
   */
  description?: string;
  /**
   * Information of the step
   */
  information?: string;
  /**
   * Category of the step
   */
  category?: Category;
  /**
   * The form field for the step
   */
  fields?: FormField[];
  /**
   * Used to retrieve fields in an async way
   */
  asyncFields?: (formGroup?: FormGroup, step?: FormStep) => Observable<FormField[]>;
  /**
   * If the step is optional
   */
  optional?: boolean;
}
