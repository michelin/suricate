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
import { MatPaginator, PageEvent } from '@angular/material/paginator';

import { HttpFilterService } from '../../services/backend/http-filter/http-filter.service';

/**
 * Component used to display the paginator
 */
@Component({
  selector: 'suricate-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
  imports: [MatPaginator]
})
export class PaginatorComponent {
  /**
   * Hide the page size
   */
  @Input()
  hidePageSize: boolean;

  /**
   * The current page to display
   */
  @Input()
  currentPage: number;

  /**
   * Number of elements per paged
   */
  @Input()
  pageNbElements: number;

  /**
   * Total of fetch elements
   */
  @Input()
  totalElements: number;

  /**
   * Event emit when the page has changed
   */
  @Output()
  pageChange = new EventEmitter<PageEvent>();

  /**
   * Used to emit an event when the page has changed
   *
   * @param pageEvent The Angular material page event
   */
  public onPageChanged(pageEvent: PageEvent): void {
    this.pageChange.emit(pageEvent);
  }

  /**
   * Returns the default size options for paginated lists
   */
  public getDefaultSizeOptions(): number[] {
    return HttpFilterService.DEFAULT_PAGE_SIZE_OPTIONS;
  }
}
