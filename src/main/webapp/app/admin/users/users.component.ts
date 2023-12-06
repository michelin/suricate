/*
 * Copyright 2012-2021 the original author or authors.
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

import { Component, Injector, OnInit } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { User } from '../../shared/models/backend/user/user';
import { Role } from '../../shared/models/backend/role/role';
import { IconEnum } from '../../shared/enums/icon.enum';
import { TitleCasePipe } from '@angular/common';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import {
  UserFormFieldsService
} from '../../shared/services/frontend/form-fields/user-form-fields/user-form-fields.service';
import { UserRequest } from '../../shared/models/backend/user/user-request';
import { HttpAdminUserService } from '../../shared/services/backend/http-admin-user/http-admin-user.service';

/**
 * Component used to display the list of users
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class UsersComponent extends ListComponent<User> implements OnInit {
  /**
   * User selected in the list for modification
   */
  private userSelected: User;

  /**
   * Constructor
   *
   * @param httpAdminUserService Manage the http calls for users as admin
   * @param userFormFieldsService Build the form fields for a user
   * @param injector Manage the injection of services
   */
  constructor(
    private readonly httpAdminUserService: HttpAdminUserService,
    private readonly userFormFieldsService: UserFormFieldsService,
    protected injector: Injector
  ) {
    super(httpAdminUserService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
    this.initFilter();
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    super.ngOnInit();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'user.list' };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.EDIT,
          tooltip: { message: 'user.edit' },
          color: 'primary',
          variant: 'miniFab',
          callback: (event: Event, user: User) => this.openFormSidenav(event, user, this.editUser.bind(this))
        },
        {
          icon: IconEnum.DELETE,
          tooltip: { message: 'user.delete' },
          color: 'warn',
          variant: 'miniFab',
          callback: (event: Event, user: User) => this.deleteUser(event, user)
        }
      ]
    };
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['username,asc'];
  }

  /**
   * {@inheritDoc}
   */
  protected getFirstLabel(user: User): string {
    return `${user.firstname} ${user.lastname} (${user.username})`;
  }

  /**
   * {@inheritDoc}
   */
  protected getSecondLabel(user: User): string {
    return user.email;
  }

  /**
   * {@inheritDoc}
   */
  protected getThirdLabel(user: User): string {
    return user.roles.map((role: Role) => role.name).join(', ');
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param user The user clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, user: User, saveCallback: (userRequest: UserRequest) => void): void {
    this.userSelected = user;

    this.sidenavService.openFormSidenav({
      title: user ? 'user.edit' : 'user.add',
      formFields: this.userFormFieldsService.generateFormFields(user),
      save: (userRequest: UserRequest) => saveCallback(userRequest)
    });
  }

  /**
   * Edit a user
   * @param userRequest The user request to make
   */
  private editUser(userRequest: UserRequest): void {
    this.httpAdminUserService.update(this.userSelected.id, userRequest).subscribe(() => {
      super.refreshList();
    });
  }

  /**
   * Function used to delete a user
   * @param event The click event
   * @param user The user to delete
   */
  private deleteUser(event: Event, user: User): void {
    const titleCasePipe = new TitleCasePipe();

    this.dialogService.confirm({
      title: 'user.delete',
      message: `${this.translateService.instant('user.delete.confirm')} ${titleCasePipe.transform(user.username)} ?`,
      accept: () => {
        this.httpAdminUserService.delete(user.id).subscribe(() => {
          this.toastService.sendMessage('user.delete.success', ToastTypeEnum.SUCCESS);
          this.refreshList();
        });
      }
    });
  }
}
