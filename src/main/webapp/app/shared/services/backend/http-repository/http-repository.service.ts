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
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';

import { Repository } from '../../../models/backend/repository/repository';
import { RepositoryRequest } from '../../../models/backend/repository/repository-request';
import { Widget } from '../../../models/backend/widget/widget';
import { AbstractHttpService } from '../abstract-http/abstract-http.service';
import { EMPTY } from 'rxjs';
import { HttpFilterService } from '../http-filter/http-filter.service';
import { HttpFilter } from '../../../models/backend/http-filter';
import { Page } from '../../../models/backend/page';

/**
 * Service that manage HTTP repository calls
 */
@Injectable({ providedIn: 'root' })
export class HttpRepositoryService implements AbstractHttpService<Repository | RepositoryRequest> {
  /**
   * Global repositories endpoint
   * @type {string}
   */
  private static readonly repositoriesApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1/repositories`;

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client to inject
   */
  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Return the list of every repositories
   */
  public getAll(filter?: HttpFilter): Observable<Page<Repository>> {
    const url = `${HttpRepositoryService.repositoriesApiEndpoint}`;

    return this.httpClient.get<Page<Repository>>(HttpFilterService.getFilteredUrl(url, filter));
  }

  /**
   * Get the repository id
   *
   * @param repositoryId The repository id
   */
  public getById(repositoryId: number): Observable<Repository> {
    const url = `${HttpRepositoryService.repositoriesApiEndpoint}/${repositoryId}`;

    return this.httpClient.get<Repository>(url);
  }

  /**
   * Add a repository
   *
   * @param repositoryRequest The repository to add
   */
  public create(repositoryRequest: RepositoryRequest): Observable<Repository> {
    const url = `${HttpRepositoryService.repositoriesApiEndpoint}`;

    return this.httpClient.post<Repository>(url, repositoryRequest);
  }

  /**
   * Update a repository
   *
   * @param repositoryId The repository id
   * @param repositoryRequest The repository with informations updated
   */
  public update(repositoryId: number, repositoryRequest: RepositoryRequest): Observable<void> {
    const url = `${HttpRepositoryService.repositoriesApiEndpoint}/${repositoryId}`;

    return this.httpClient.put<void>(url, repositoryRequest);
  }

  /**
   * Delete a repository
   *
   * @param id The repository id to delete
   */
  public delete(id: number): Observable<void> {
    return EMPTY;
  }

  /**
   * Get the list of widgets for a repository
   *
   * @param repositoryId The repository ID
   */
  public getRepositoryWidgets(repositoryId: number): Observable<Widget[]> {
    const url = `${HttpRepositoryService.repositoriesApiEndpoint}/${repositoryId}/widgets`;

    return this.httpClient.get<Widget[]>(url);
  }
}
