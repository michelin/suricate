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

import { Observable } from 'rxjs';

import { ButtonColorEnum } from '../../../enums/button-color.enum';
import { ButtonTypeEnum } from '../../../enums/button-type.enum';
import { IconEnum } from '../../../enums/icon.enum';
import { TooltipConfiguration } from '../tooltip/tooltip-configuration';

/**
 * Model of Button
 */
export class ButtonConfiguration<T> {
  label?: string;
  icon?: IconEnum;
  color?: ButtonColorEnum;
  callback?: (event: Event, object?: T) => void;
  disabled?: Observable<boolean>;
  hidden?: (object?: T) => boolean;
  type?: ButtonTypeEnum;
  variant?: 'flat' | 'miniFab';
  tooltip?: TooltipConfiguration;
}
