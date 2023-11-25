/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

import { KtdGridLayoutItem } from '@katoid/angular-grid-layout/lib/grid.definitions';

/**
 * Class that manage the utility function for grid items management
 */
export class GridItemUtils {
  /**
   * Check if item have been moved
   *
   * @param beforeEvent Item state before move action
   * @param afterEvent Item state after move action
   */
  public static isItemHaveBeenMoved(beforeEvent: KtdGridLayoutItem, afterEvent: KtdGridLayoutItem) {
    return (
      beforeEvent.w !== afterEvent.w ||
      beforeEvent.h !== afterEvent.h ||
      beforeEvent.x !== afterEvent.x ||
      beforeEvent.y !== afterEvent.y
    );
  }
}
