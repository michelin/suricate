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

import { UserListComponent } from './components/user-list/user-list.component';
import { AuthGuard } from '../shared/guards/auth.guard';
import { UserEditComponent } from './components/user-edit/user-edit.component';
import { AdminGuard } from '../shared/guards/admin.guard';
import { ConfigListComponent } from './components/config-list/config-list.component';
import { RepositoryListComponent } from './components/repository-list/repository-list.component';
import { RepositoryAddEditComponent } from './components/repository-add-edit/repository-add-edit.component';

export const adminRoutes: Routes = [
  {
    path: 'security/users',
    component: UserListComponent,
    canActivate: [AuthGuard, AdminGuard],
    data: {
      breadcrumb: 'User List'
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
    path: 'security/config',
    component: ConfigListComponent,
    canActivate: [AuthGuard, AdminGuard],
    data: {
      breadcrumb: 'Config'
    }
  },
  {
    path: 'repositories',
    component: RepositoryListComponent,
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
