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
import { CommonModule } from '@angular/common';

import { AddWidgetDialogComponent } from './header/components/add-widget-dialog/add-widget-dialog.component';
import { DashboardActionsComponent } from './header/components/dashboard-actions/dashboard-actions.component';
import { PagesFooterComponent } from './footer/pages/pages-footer.component';
import { TvManagementDialogComponent } from './header/components/tv-management-dialog/tv-management-dialog.component';
import { WidgetListActionsComponent } from './header/components/widget-list-actions/widget-list-actions.component';
import { SharedModule } from '../shared/shared.module';
import { PagesHeaderComponent } from './header/pages/pages-header.component';
import { SidenavComponent } from './sidenav/pages/sidenav.component';
import { RepositoryActionsComponent } from './header/components/repository-actions/repository-actions.component';

@NgModule({
  imports: [CommonModule, SharedModule],
  declarations: [
    AddWidgetDialogComponent,
    DashboardActionsComponent,
    PagesFooterComponent,
    PagesHeaderComponent,
    SidenavComponent,
    TvManagementDialogComponent,
    WidgetListActionsComponent,
    RepositoryActionsComponent
  ],
  entryComponents: [AddWidgetDialogComponent, TvManagementDialogComponent],
  exports: [AddWidgetDialogComponent, PagesFooterComponent, PagesHeaderComponent, SidenavComponent]
})
export class LayoutModule {}
