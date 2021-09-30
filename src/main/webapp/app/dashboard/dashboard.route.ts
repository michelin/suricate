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

import { DashboardTvComponent } from './components/dashboard-tv/dashboard-tv.component';
import { AuthGuard } from '../shared/guards/auth/auth.guard';
import { AdminGuard } from '../shared/guards/admin/admin.guard';
import { DashboardDetailComponent } from './components/dashboard-detail/dashboard-detail.component';
import { DashboardsComponent } from '../admin/dashboards/dashboards.component';
import { ProjectWidgetWizardComponent } from './components/wizard/project-widget-wizard.component';
import {RotationDetailComponent} from "./components/rotation-detail/rotation-detail.component";

export const DashboardRoutes: Routes = [
  {
    path: 'tv',
    component: DashboardTvComponent
  },
  {
    path: 'dashboards/:dashboardToken',
    component: DashboardDetailComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'rotations/:rotationToken',
    component: RotationDetailComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboards/:dashboardToken/widgets/create',
    component: ProjectWidgetWizardComponent,
    canActivate: [AuthGuard]
  }
];
