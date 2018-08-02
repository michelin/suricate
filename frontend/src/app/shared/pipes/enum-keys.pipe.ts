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

/**
 * Get the keys of a map to iterate over it
 */
@Pipe({
  name: 'enumKeys'
})
export class EnumKeysPipe implements PipeTransform {

  /**
   * Transform an enum to return the list of keys
   *
   * @param enumObject The enum object
   */
  transform(enumObject: any): any {
    return Object.keys(enumObject);
  }

}
