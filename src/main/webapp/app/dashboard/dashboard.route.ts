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

import { authGuard } from '../shared/guards/auth/auth.guard';
import { DashboardDetailComponent } from './components/dashboard-detail/dashboard-detail.component';
import { DashboardTvComponent } from './components/dashboard-tv/dashboard-tv.component';
import { AddWidgetToProjectWizardComponent } from './components/wizard/add-widget-to-project-wizard/add-widget-to-project-wizard.component';

export const dashboardRoutes: Routes = [
	{
		path: 'tv',
		component: DashboardTvComponent
	},
	{
		path: 'dashboards/:dashboardToken/:gridId',
		component: DashboardDetailComponent,
		canActivate: [authGuard],
		canActivateChild: [authGuard]
	},
	{
		path: 'dashboards/:dashboardToken/:gridId/widgets/create',
		component: AddWidgetToProjectWizardComponent,
		canActivate: [authGuard],
		canActivateChild: [authGuard]
	}
];
