/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource, MatDialog} from '@angular/material';

import {of as observableOf} from 'rxjs/observable/of';
import {catchError} from 'rxjs/operators';
import {merge} from 'rxjs/observable/merge';
import {startWith} from 'rxjs/operators/startWith';
import {switchMap} from 'rxjs/operators/switchMap';
import {map} from 'rxjs/operators/map';
import {UserService} from '../user.service';
import {Role} from '../../../shared/model/dto/user/Role';
import {DeleteUserDialogComponent} from '../components/delete-user-dialog/delete-user-dialog.component';
import {User} from '../../../shared/model/dto/user/User';

/**
 * This component is used for displaying the list of users
 */
@Component({
  selector: 'app-user',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements AfterViewInit {
  /**
   * The object that hold data management
   * @type {MatTableDataSource<User>}  The mat table of users
   */
  matTableDataSource = new MatTableDataSource<User>();

  /**
   * Column displayed on the mat table
   * @type {string[]} The list of column references
   */
  displayedColumns = ['username', 'fullname', 'mail', 'roles', 'edit', 'delete'];
  /**
   * Management of the spinner
   * @type {boolean} True when we are loading result, false otherwise
   */
  isLoadingResults = false;
  /**
   * If we have an error while displaying the table
   * @type {boolean}
   */
  errorCatched = false;
  /**
   * The number of users
   * @type {number}
   */
  resultsLength = 0;

  /**
   * Management of the table sorting
   */
  @ViewChild(MatSort) matSort: MatSort;
  /**
   * Management of the table pagination
   */
  @ViewChild(MatPaginator) matPaginator: MatPaginator;

  /**
   * The constructor
   *
   * @param {UserService} userService The user service to inject
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service to inject
   * @param {MatDialog} matDialog The mat dialog service to inject
   */
  constructor(private userService: UserService,
              private changeDetectorRef: ChangeDetectorRef,
              private matDialog: MatDialog) { }

  /**
   * Called when the view has been init
    */
  ngAfterViewInit() {
    this.initUsersTable();
  }

  /**
   * Display every user inside the table
   */
  initUsersTable(): void {
    // If the user changes the sort order, reset back to the first page.
    merge(this.matSort.sortChange, this.matPaginator.page)
        .pipe(
            startWith(null),
            switchMap(() => {
              this.isLoadingResults = true;
              this.changeDetectorRef.detectChanges();
              return this.userService.getAll();
            }),
            map(data => {
              this.isLoadingResults = false;
              this.errorCatched = false;

              return data;
            }),
            catchError(() => {
              this.isLoadingResults = false;
              this.errorCatched = true;

              return observableOf([]);
            })
        )
        .subscribe(data =>  {
          this.resultsLength = data.length;
          this.matTableDataSource.data = data;
          this.matTableDataSource.sort = this.matSort;
        });

    this.matTableDataSource.sortingDataAccessor = (item: any, property) => {
      switch (property) {
        case 'roles':
          return this.getRolesName(item.roles);
        default:
          return item[property];
      }
    };
  }

  /**
   * Get the roles name for nested object management (display, sort)
   *
   * @param {Role[]} roles The list of roles to display
   * @returns {string} The list of roles has string
   */
  getRolesName(roles: Role[]): string {
    return roles.map(role => role.name).toString();
  }

  /**
   * Open the delete user dialog
   * @param {User} user The user to delete
   */
  openDialogDeleteUser(user: User) {
    const deleteUserDialogRef = this.matDialog.open(DeleteUserDialogComponent, {
      data: {user: user}
    });

    deleteUserDialogRef.afterClosed().subscribe(shouldDelete => {

    });
  }

}
