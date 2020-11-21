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
import { HttpFilter } from '../../../models/backend/http-filter';

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
   * Used to ask for every elements in one request
   */
  public static readonly INFINITE_PAGE_SIZE = 2000;

  /**
   * Default page size options
   */
  public static readonly DEFAULT_PAGE_SIZE_OPTIONS = [5, 10, 20, 50];

  /**
   * Get the default filter
   *
   * @param search A string defining a filter on a field to apply on the research
   */
  public static getDefaultFilter(search?: string): HttpFilter {
    const httpFilter = new HttpFilter();
    httpFilter.size = HttpFilterService.DEFAULT_PAGE_SIZE_OPTIONS[1];
    httpFilter.page = HttpFilterService.DEFAULT_PAGE;

    if (search) {
      httpFilter.search = search;
    }

    return httpFilter;
  }

  /**
   * Get infinite page filter
   *
   * @param sort A string defining the order to apply
   */
  public static getInfiniteFilter(sort?: string[]): HttpFilter {
    const httpFilter = new HttpFilter();
    httpFilter.size = HttpFilterService.INFINITE_PAGE_SIZE;
    httpFilter.page = HttpFilterService.DEFAULT_PAGE;

    if (sort) {
      httpFilter.sort = sort;
    }

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
