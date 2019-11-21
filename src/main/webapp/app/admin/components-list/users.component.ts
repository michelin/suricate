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

import { Component, Injector, OnInit } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { User } from '../../shared/models/backend/user/user';
import { HttpUserService } from '../../shared/services/backend/http-user.service';
import { Role } from '../../shared/models/backend/role/role';
import { IconEnum } from '../../shared/enums/icon.enum';
import { TitleCasePipe } from '@angular/common';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { HttpRoleService } from '../../shared/services/backend/http-role.service';
import { UserFormFieldsService } from '../../shared/form-fields/user-form-fields.service';
import { UserRequest } from '../../shared/models/backend/user/user-request';

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
   * @type {User}
   * @private
   */
  private userSelected: User;

  /**
   * Constructor
   *
   * @param {HttpUserService} httpUserService Suricate service used to manage the http calls for users
   * @param {HttpRoleService} httpRoleService Suricate service used to manage the http calls for roles
   * @param {UserFormFieldsService} userFormFieldsService Frontend service used to build the form fields for a user
   * @param {Injector} injector Angular Service used to manage the injection of services
   */
  constructor(
    private readonly httpUserService: HttpUserService,
    private readonly httpRoleService: HttpRoleService,
    private readonly userFormFieldsService: UserFormFieldsService,
    protected injector: Injector
  ) {
    super(httpUserService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
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
    this.headerConfiguration = { title: 'users.list' };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, user: User) => this.openFormSidenav(event, user, this.editUser.bind(this))
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          callback: (event: Event, user: User) => this.deleteUser(event, user)
        }
      ]
    };
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

    this.translateService.get(['user.edit', 'user.add']).subscribe((translations: string[]) => {
      this.userFormFieldsService.generateFormFields(user).subscribe((formFields: FormField[]) => {
        this.sidenavService.openFormSidenav({
          title: user ? translations['user.edit'] : translations['user.add'],
          formFields: formFields,
          save: (userRequest: UserRequest) => saveCallback(userRequest)
        });
      });
    });
  }

  /**
   * Edit a user
   *
   * @param userRequest The user request to make
   */
  private editUser(userRequest: UserRequest): void {
    this.httpUserService.update(this.userSelected.id, userRequest).subscribe(() => {
      super.refreshList();
    });
  }

  /**
   * Function used to delete a user
   * @param event The click event
   * @param user The user to delete
   */
  private deleteUser(event: Event, user: User): void {
    this.translateService.get(['user.delete', 'delete.confirm']).subscribe((translations: string[]) => {
      const titlecasePipe = new TitleCasePipe();

      this.dialogService.confirm({
        title: translations['user.delete'],
        message: `${translations['delete.confirm']} ${titlecasePipe.transform(user.username)}`,
        accept: () => {
          this.httpUserService.delete(user.id).subscribe(() => {
            this.toastService.sendMessage('User deleted successfully', ToastTypeEnum.SUCCESS);
            this.refreshList();
          });
        }
      });
    });
  }
}
