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

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/internal/Observable';

import {Repository} from '../../../../../shared/model/api/Repository';
import {repositoriesApiEndpoint} from '../../../../../app.constant';

/**
 * Service that manage HTTP repository calls
 */
@Injectable({
  providedIn: 'root'
})
export class RepositoryService {

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client to inject
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Return the list of every repositories
   */
  getAll(): Observable<Repository[]> {
    const url = `${repositoriesApiEndpoint}`;

    return this.httpClient.get<Repository[]>(url);
  }

  /**
   * Get the repository id
   *
   * @param repositoryId The repository id
   */
  getOneById(repositoryId: number): Observable<Repository> {
    const url = `${repositoriesApiEndpoint}/${repositoryId}`;

    return this.httpClient.get<Repository>(url);
  }

  /**
   * Add a repo
   *
   * @param repository The repository to add
   */
  addOne(repository: Repository): Observable<Repository> {
    const url = `${repositoriesApiEndpoint}`;

    return this.httpClient.put<Repository>(url, repository);
  }

  /**
   * Update a repository
   *
   * @param repositoryId The repository id
   * @param repository The repository with informations updated
   * @returns The repository as observable
   */
  updateOneById(repositoryId: number, repository: Repository): Observable<Repository> {
    const url = `${repositoriesApiEndpoint}/${repositoryId}`;

    return this.httpClient.put<Repository>(url, repository);
  }
}
