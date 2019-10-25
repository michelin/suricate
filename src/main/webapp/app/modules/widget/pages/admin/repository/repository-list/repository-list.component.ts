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

import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { merge, of } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';

import { HttpRepositoryService } from '../../../../../../shared/services/backend/http-repository.service';
import { Repository } from '../../../../../../shared/models/backend/repository/repository';
import { ToastService } from '../../../../../../shared/services/frontend/toast.service';
import { ToastTypeEnum } from '../../../../../../shared/enums/toast-type.enum';

@Component({
  selector: 'app-repository-list',
  templateUrl: './repository-list.component.html',
  styleUrls: ['./repository-list.component.scss']
})
export class RepositoryListComponent implements OnInit {
  /**
   * manage sort of the Mat table
   * @type {MatSort}
   */
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  /**
   * manage pagination of the Mat table
   * @type {MatPaginator}
   */
  @ViewChild(MatPaginator, { static: true }) matPaginator: MatPaginator;

  /**
   * Hold the data of the mat table
   * @type {MatTableDataSource<any>}
   */
  matTableDataSource = new MatTableDataSource();
  /**
   * The column references of the mat table
   * @type {string[]}
   */
  displayedColumns = ['name', 'localPath', 'url', 'branch', 'type', 'status', 'edit'];
  /**
   * Is the results are loading
   * @type {boolean}
   */
  isLoadingResults = false;
  /**
   * If we have an error
   * @type {boolean}
   */
  errorCatched = false;
  /**
   * The number of rows
   * @type {number}
   */
  resultsLength = 0;

  /**
   * Constructor
   *
   * @param {ChangeDetectorRef} changeDetectorRef The change detector ref to inject
   * @param {HttpRepositoryService} repositoryService The repository service
   * @param {ToastService} toastService The toast service
   */
  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private repositoryService: HttpRepositoryService,
    private toastService: ToastService
  ) {}

  /**
   * Called when the component is created
   */
  ngOnInit() {
    this.initTable();
  }

  /**
   * Init the list of repo
   */
  initTable() {
    merge(this.matSort.sortChange, this.matPaginator.page)
      .pipe(
        startWith(null),
        switchMap(() => {
          this.isLoadingResults = true;
          this.changeDetectorRef.detectChanges();
          return this.repositoryService.getAll();
        }),
        map(data => {
          this.isLoadingResults = false;
          this.errorCatched = false;

          return data;
        }),
        catchError(() => {
          this.isLoadingResults = false;
          this.errorCatched = true;

          return of([]);
        })
      )
      .subscribe(data => {
        this.resultsLength = data.length;
        this.matTableDataSource.data = data;
        this.matTableDataSource.sort = this.matSort;
      });
  }

  /**
   * Enable or disable a repository
   *
   * @param {Repository} repository The repository to disable / enable
   * @param {MatSlideToggleChange} changeEvent when click on the toggle slider
   */
  toggleWidgetActivation(repository: Repository, changeEvent: MatSlideToggleChange) {
    repository.enabled = changeEvent.checked;

    this.repositoryService.updateOneById(repository.id, repository).subscribe(() => {
      const repoStatusAsString: string = repository.enabled ? 'activated' : 'disabled';
      this.toastService.sendMessage(`The repository ${repository.name} has been ${repoStatusAsString} successfully`, ToastTypeEnum.SUCCESS);
    });
  }
}
