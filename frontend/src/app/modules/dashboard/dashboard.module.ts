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
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../shared/shared.module';

import {DashboardService} from './dashboard.service';
import {DashboardDetailComponent} from './pages/dashboard-detail/dashboard-detail.component';
import {DashboardListComponent} from './pages/admin/dashboard-list/dashboard-list.component';
import {DashboardEditComponent} from './pages/admin/dashboard-edit/dashboard-edit.component';
import {DeleteDashboardDialogComponent} from './components/delete-dashboard-dialog/delete-dashboard-dialog.component';
import {DeleteProjectWidgetDialogComponent} from './components/delete-project-widget-dialog/delete-project-widget-dialog.component';
import {EditProjectWidgetDialogComponent} from './components/edit-project-widget-dialog/edit-project-widget-dialog.component';
import {DashboardScreenComponent} from './components/dashboard-screen/dashboard-screen.component';
import {DashboardTvComponent} from './pages/dashboard-tv/dashboard-tv.component';
import {ScreenService} from './screen.service';
import {LayoutModule} from '../../layout/layout.module';
import {DashboardRoutes} from './dashboard.route';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(DashboardRoutes),
    LayoutModule,
    SharedModule
  ],
  declarations: [
    DashboardDetailComponent,
    DashboardListComponent,
    DashboardEditComponent,
    DeleteDashboardDialogComponent,
    DeleteProjectWidgetDialogComponent,
    EditProjectWidgetDialogComponent,
    DashboardScreenComponent,
    DashboardTvComponent
  ],
  exports: [
    RouterModule
  ],
  providers: [
    DashboardService,
    ScreenService
  ],
  entryComponents: [
    DeleteDashboardDialogComponent,
    DeleteProjectWidgetDialogComponent,
    EditProjectWidgetDialogComponent
  ]
})
export class DashboardModule {
}
