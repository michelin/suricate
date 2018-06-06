/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Asset} from './Asset';
import {WidgetParam} from './WidgetParam';
import {Category} from './Category';
import {WidgetAvailabilityEnum} from './enums/WidgetAvailabilityEnum';

export class Widget {
  id: number;
  name: string;
  description: string;
  technicalName: string;
  htmlContent: string;
  cssContent: string;
  backendJs: string;
  info: string;
  delay: number;
  timeout: string;
  image: Asset;
  category: Category;
  widgetAvailability: WidgetAvailabilityEnum;
  widgetParams: WidgetParam[];
}
