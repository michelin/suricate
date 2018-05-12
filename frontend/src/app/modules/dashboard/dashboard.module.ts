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

import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {AuthGuard} from '../../shared/guards/auth.guard';
import {DashboardService} from './dashboard.service';
import { DashboardDetailComponent } from './dashboard-detail/dashboard-detail.component';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../../shared/shared.module';
import {DashboardListComponent} from './dashboard-list/dashboard-list.component';
import { DashboardEditComponent } from './dashboard-edit/dashboard-edit.component';
import { DeleteDashboardDialogComponent } from './components/delete-dashboard-dialog/delete-dashboard-dialog.component';

const dashboardRoutes: Routes = [
  { path: 'dashboard/:id', component: DashboardDetailComponent, canActivate: [AuthGuard] },
  { path: 'dashboards', component: DashboardListComponent, canActivate: [AuthGuard] },
  {
    path: 'dashboards/:dashboardId/edit',
    component: DashboardEditComponent,
    data: { breadcrumb: 'Edit Dashboard' },
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [
      CommonModule,
      RouterModule.forChild(dashboardRoutes),
      SharedModule
  ],
  declarations: [
      DashboardDetailComponent,
      DashboardListComponent,
      DashboardEditComponent,
      DeleteDashboardDialogComponent
  ],
  exports: [
      RouterModule
  ],
  providers: [
      DashboardService
  ],
  entryComponents: [
      DeleteDashboardDialogComponent
  ]
})
export class DashboardModule {}
