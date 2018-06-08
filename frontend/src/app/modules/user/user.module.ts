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

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {UserListComponent} from './user-list/user-list.component';
import {AuthGuard} from '../../shared/auth/guards/auth.guard';
import {UserService} from './user.service';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../../shared/shared.module';
import {DeleteUserDialogComponent} from './components/delete-user-dialog/delete-user-dialog.component';
import {UserEditComponent} from './user-edit/user-edit.component';

const appRoutes: Routes = [
  {path: 'users', component: UserListComponent, data: {breadcrumb: 'User List'}, canActivate: [AuthGuard]},
  {path: 'users/:userId/edit', component: UserEditComponent, data: {breadcrumb: 'Edit User'}, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(appRoutes),
    SharedModule
  ],
  declarations: [
    UserListComponent,
    DeleteUserDialogComponent,
    UserEditComponent
  ],
  entryComponents: [
    DeleteUserDialogComponent
  ],
  exports: [
    RouterModule
  ],
  providers: [
    UserService
  ]
})
export class UserModule {
}
