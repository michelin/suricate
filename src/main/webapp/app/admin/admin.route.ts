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

import { Routes } from '@angular/router';

import { AuthGuard } from '../shared/guards/auth.guard';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { AdminGuard } from '../shared/guards/admin.guard';
import { RepositoryAddEditComponent } from './components/repository-add-edit/repository-add-edit.component';
import { UsersComponent } from './components-list/users.component';
import { RepositoriesComponent } from './components-list/repositories.component';

export const adminRoutes: Routes = [
  {
    path: 'admin/users',
    component: UsersComponent,
    canActivate: [AuthGuard, AdminGuard],
    data: {
      breadcrumb: 'Users'
    }
  },
  {
    path: 'security/users/:userId/edit',
    component: UserEditComponent,
    canActivate: [AuthGuard, AdminGuard],
    data: {
      breadcrumb: 'Edit User'
    }
  },
  {
    path: 'repositories',
    component: RepositoriesComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'repositories/add',
    component: RepositoryAddEditComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'repositories/:repositoryId/edit',
    component: RepositoryAddEditComponent,
    canActivate: [AuthGuard, AdminGuard]
  }
];
