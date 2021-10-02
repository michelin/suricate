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
import {RouterModule} from '@angular/router';

import {SharedModule} from '../shared/shared.module';
import {DashboardDetailComponent} from './components/dashboard-detail/dashboard-detail.component';
import {DashboardScreenComponent} from './components/dashboard-screen/dashboard-screen.component';
import {DashboardTvComponent} from './components/dashboard-tv/dashboard-tv.component';
import {LayoutModule} from '../layout/layout.module';
import {DashboardRoutes} from './dashboard.route';
import {DashboardScreenWidgetComponent} from './components/dashboard-screen/dashboard-screen-widget/dashboard-screen-widget.component';
import {ProjectWidgetWizardComponent} from './components/wizard/project-widget-wizard.component';
import {TvManagementDialogComponent} from './components/tv-management-dialog/tv-management-dialog.component';
import { RotationDetailComponent } from './components/rotation-detail/rotation-detail.component';
import { DashboardTvManagementDialogComponent } from './components/tv-management-dialog/dashboard-tv-management-dialog/dashboard-tv-management-dialog.component';
import { RotationTvManagementDialogComponent } from './components/tv-management-dialog/rotation-tv-management-dialog/rotation-tv-management-dialog.component';

@NgModule({
  imports: [RouterModule.forChild(DashboardRoutes), LayoutModule, SharedModule],
  declarations: [
    DashboardDetailComponent,
    DashboardScreenComponent,
    DashboardScreenWidgetComponent,
    DashboardTvComponent,
    ProjectWidgetWizardComponent,
    RotationDetailComponent,
    DashboardTvManagementDialogComponent,
    RotationTvManagementDialogComponent
  ],
  entryComponents: [],
  exports: [RouterModule]
})
export class DashboardModule {}
