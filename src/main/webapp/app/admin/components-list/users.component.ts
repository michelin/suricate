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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DataTypeEnum } from '../../shared/enums/data-type.enum';
import { Validators } from '@angular/forms';
import { CustomValidators } from 'ng2-validation';
import { FormOption } from '../../shared/models/frontend/form/form-option';
import { HttpRoleService } from '../../shared/services/backend/http-role.service';

/**
 * Component used to display the list of users
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class UsersComponent extends ListComponent<User> implements OnInit {
  /**
   * The list of roles
   */
  private roles: Role[];

  /**
   * Constructor
   *
   * @param httpUserService Suricate service used to manage the http calls for users
   * @param httpRoleService Suricate service used to manage the http calls for roles
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private httpUserService: HttpUserService, private httpRoleService: HttpRoleService, protected injector: Injector) {
    super(httpUserService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.httpRoleService.getRoles().subscribe((roles: Role[]) => {
      this.roles = roles;
    });
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'users.list'
    };
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
          callback: (event: Event, user: User) => this.openFormSidenav(event, user, this.editUser)
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
    return `${user.fullname} (${user.username})`;
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
   * Edit a user
   */
  private editUser(): void {}

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param user The user clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, user: User, saveCallback: () => void): void {
    this.translateService.get(['user.edit', 'user.add']).subscribe((translations: string[]) => {
      this.getFormFields(user).subscribe((formFields: FormField[]) => {
        this.sidenavService.openFormSidenav({
          title: user ? translations['user.edit'] : translations['user.add'],
          formFields: formFields,
          save: () => saveCallback()
        });
      });
    });
  }

  /**
   * Build the form fields of the user
   *
   * @param user The bean
   */
  private getFormFields(user?: User): Observable<FormField[]> {
    return this.translateService.get(['username', 'firstname', 'lastname', 'email', 'roles']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'username',
            label: translations['username'],
            type: DataTypeEnum.TEXT,
            value: user.username ? user.username : null,
            readOnly: true,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'android'
          },
          {
            key: 'firstname',
            label: translations['firstname'],
            type: DataTypeEnum.TEXT,
            value: user.firstname ? user.firstname : null,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'lastname',
            label: translations['lastname'],
            type: DataTypeEnum.TEXT,
            value: user.lastname ? user.lastname : null,
            validators: [Validators.required, Validators.minLength(3)],
            matIconPrefix: 'person'
          },
          {
            key: 'email',
            label: translations['email'],
            type: DataTypeEnum.TEXT,
            value: user.email ? user.email : null,
            validators: [Validators.required, CustomValidators.email],
            matIconPrefix: 'email'
          },
          {
            key: 'roles',
            label: translations['roles'],
            type: DataTypeEnum.MULTIPLE,
            value: user.roles && user.roles.length > 0 ? user.roles.map(role => role.name) : null,
            options: this.getRoleOptions(),
            validators: [Validators.required]
          }
        ];
      })
    );
  }

  /**
   * Get the role options
   */
  getRoleOptions(): FormOption[] {
    const roleOptions: FormOption[] = [];
    this.roles.forEach((role: Role) => {
      roleOptions.push({
        key: role.name,
        label: role.description
      });
    });

    return roleOptions;
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
            this.refreshList();
            this.toastService.sendMessage('User deleted successfully', ToastTypeEnum.SUCCESS);
          });
        }
      });
    });
  }
}
