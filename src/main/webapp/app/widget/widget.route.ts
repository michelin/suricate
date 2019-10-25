/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { Routes } from '@angular/router';

import { AuthGuard } from '../shared/guards/auth.guard';
import { WidgetListComponent } from './components/widget-list/widget-list.component';
import { WidgetConfigurationListComponent } from './components/configuration-list/widget-configuration-list.component';
import { AdminGuard } from '../shared/guards/admin.guard';
import { WidgetConfigurationEditComponent } from './components/configuration-edit/widget-configuration-edit.component';

export const widgetRoutes: Routes = [
  {
    path: 'widgets/catalog',
    component: WidgetListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'widgets/configurations',
    component: WidgetConfigurationListComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'widgets/configurations/:configurationKey/edit',
    component: WidgetConfigurationEditComponent,
    canActivate: [AuthGuard, AdminGuard]
  }
];
