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

import {Pipe, PipeTransform} from '@angular/core';

import {Configuration} from '../model/api/configuration/Configuration';
import {DataType} from '../model/enums/DataType';

/**
 * Transform a string into stars
 */
@Pipe({
  name: 'password'
})
export class PasswordPipe implements PipeTransform {

  /**
   * The constructor
   */
  constructor() {
  }

  /**
   * The transform function
   *
   * @param {Configuration} config The string value to sanitize
   * @returns {String} The password hidden
   */
  transform(config: Configuration): String {
    return (config.dataType && config.dataType === DataType.PASSWORD) ? '*****' : config.value;
  }

}
