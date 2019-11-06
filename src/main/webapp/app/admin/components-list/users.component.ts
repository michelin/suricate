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

import { Component, Injector } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { User } from '../../shared/models/backend/user/user';
import { HttpUserService } from '../../shared/services/backend/http-user.service';
import { Role } from '../../shared/models/backend/role/role';
import { IconEnum } from '../../shared/enums/icon.enum';
import { TitleCasePipe } from '@angular/common';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';

/**
 * Component used to display the list of users
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class UsersComponent extends ListComponent<User> {
  /**
   * Constructor
   *
   * @param httpUserService Suricate service used to manage the http calls for a user
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private httpUserService: HttpUserService, protected injector: Injector) {
    super(httpUserService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
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
          callback: (event: Event, user: User) => this.editUser(event, user)
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
   * Redirect on the edit page
   *
   * @param event The click event
   * @param user The user clicked on the list
   */
  private editUser(event: Event, user: User): void {
    this.router.navigate(['/security', 'users', user.id, 'edit']);
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
