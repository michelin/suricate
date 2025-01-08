/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Routes } from '@angular/router';

import { adminGuard } from '../shared/guards/admin/admin.guard';
import { authGuard } from '../shared/guards/auth/auth.guard';
import { ConfigurationsComponent } from './configurations/configurations.component';
import { DashboardsComponent } from './dashboards/dashboards.component';
import { RepositoriesComponent } from './repositories/repositories.component';
import { UsersComponent } from './users/users.component';

export const adminRoutes: Routes = [
  {
    path: 'admin/dashboards',
    component: DashboardsComponent,
    canActivate: [authGuard, adminGuard],
    canActivateChild: [authGuard, adminGuard]
  },
  {
    path: 'admin/users',
    component: UsersComponent,
    canActivate: [authGuard, adminGuard],
    canActivateChild: [authGuard, adminGuard],
    data: {
      breadcrumb: 'Users'
    }
  },
  {
    path: 'admin/repositories',
    component: RepositoriesComponent,
    canActivate: [authGuard, adminGuard],
    canActivateChild: [authGuard, adminGuard]
  },
  {
    path: 'admin/configurations',
    component: ConfigurationsComponent,
    canActivate: [authGuard, adminGuard],
    canActivateChild: [authGuard, adminGuard]
  }
];
