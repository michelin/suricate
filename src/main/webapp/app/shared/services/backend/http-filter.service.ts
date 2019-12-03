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

import { Injectable } from '@angular/core';
import { HttpFilter } from '../../models/backend/http-filter';

/**
 * Service class that manage the instantiations of http-filters
 */
@Injectable({ providedIn: 'root' })
export class HttpFilterService {
  /**
   * Ask for the first page
   */
  public static readonly DEFAULT_PAGE = 0;
  /**
   * Default page size (number of elements return)
   */
  public static readonly DEFAULT_PAGE_SIZE = 6;
  /**
   * Used to ask for every elements in one request
   */
  public static readonly INFINITE_PAGE_SIZE = 2000;

  /**
   * Get the default filter
   */
  public static getDefaultFilter(): HttpFilter {
    const httpFilter = new HttpFilter();
    httpFilter['size'] = HttpFilterService.DEFAULT_PAGE_SIZE;
    httpFilter['page'] = HttpFilterService.DEFAULT_PAGE;

    return httpFilter;
  }

  /**
   * Get infinit page filter
   */
  public static getInfiniteFilter(): HttpFilter {
    const httpFilter = new HttpFilter();
    httpFilter['size'] = HttpFilterService.INFINITE_PAGE_SIZE;
    httpFilter['page'] = HttpFilterService.DEFAULT_PAGE;

    return httpFilter;
  }

  /**
   * Build query param using filters
   *
   * @param url The url
   * @param httpFilter The filter used to build the url
   */
  static getFilteredUrl(url: string, httpFilter: HttpFilter = HttpFilterService.getDefaultFilter()): string {
    Object.keys(httpFilter).forEach(filterKey => {
      if (httpFilter[filterKey]) {
        if (httpFilter[filterKey] instanceof Array) {
          httpFilter[filterKey].forEach(filterKeyValue => {
            url += `&${filterKey}=${encodeURIComponent(filterKeyValue)}`;
          });
        } else {
          url += `&${filterKey}=${encodeURIComponent(httpFilter[filterKey])}`;
        }
      }
    });

    if (url.includes('&')) {
      url = url.replace('&', '?');
    }

    return url;
  }
}
