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
import {Routes} from '@angular/router';

import {DashboardTvComponent} from './pages/dashboard-tv/dashboard-tv.component';
import {AuthGuard} from '../../shared/auth/guards/auth.guard';
import {DashboardListComponent} from './pages/admin/dashboard-list/dashboard-list.component';
import {AdminGuard} from '../../shared/auth/guards/admin.guard';
import {DashboardEditComponent} from './pages/admin/dashboard-edit/dashboard-edit.component';
import {DashboardDetailComponent} from './pages/dashboard-detail/dashboard-detail.component';

export const DashboardRoutes: Routes = [
  {path: 'tv', component: DashboardTvComponent},
  {path: 'dashboards/all', component: DashboardListComponent, canActivate: [AuthGuard, AdminGuard]},
  {
    path: 'dashboards/all/:dashboardToken/edit',
    component: DashboardEditComponent,
    data: {breadcrumb: 'Edit Dashboard'},
    canActivate: [AuthGuard, AdminGuard]
  },
  {path: 'dashboards/:dashboardToken', component: DashboardDetailComponent, canActivate: [AuthGuard]},
];
