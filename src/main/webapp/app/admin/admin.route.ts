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

import { Routes } from '@angular/router';

import { AuthGuard } from '../shared/guards/auth/auth.guard';
import { AdminGuard } from '../shared/guards/admin/admin.guard';
import { UsersComponent } from './users/users.component';
import { RepositoriesComponent } from './repositories/repositories.component';
import { DashboardsComponent } from './dashboards/dashboards.component';
import { ConfigurationsComponent } from './configurations/configurations.component';

export const adminRoutes: Routes = [
  {
    path: 'admin/dashboards',
    component: DashboardsComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'admin/users',
    component: UsersComponent,
    canActivate: [AuthGuard, AdminGuard],
    data: {
      breadcrumb: 'Users'
    }
  },
  {
    path: 'admin/repositories',
    component: RepositoriesComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'admin/configurations',
    component: ConfigurationsComponent,
    canActivate: [AuthGuard, AdminGuard]
  }
];
