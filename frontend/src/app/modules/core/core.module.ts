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

import { NgModule } from '@angular/core';
import { SidenavComponent } from './sidenav/sidenav.component';
import { PagesHeaderComponent } from '../../shared/components/pages-header/pages-header.component';
import { AddWidgetDialogComponent } from '../../shared/components/pages-header/components/add-widget-dialog/add-widget-dialog.component';
import { HomeComponent } from './home/home.component';
import {RouterModule, Routes} from '@angular/router';
import {HeaderDashboardSharedService} from './header-dashboard-shared.service';
import {WidgetService} from './widget.service';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../../shared/shared.module';

const coreRoutes: Routes = [
  { path: 'home', component: HomeComponent }
];

@NgModule({
  imports: [
      CommonModule,
      RouterModule.forChild(coreRoutes),
      SharedModule
  ],
  declarations: [
      SidenavComponent,
      HomeComponent
  ],
  exports: [
      SidenavComponent,
      HomeComponent
  ],
  providers: [
      HeaderDashboardSharedService,
      WidgetService
  ]
})
export class CoreModule { }
