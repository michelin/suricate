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

import { AddWidgetDialogComponent } from './components/header/components/add-widget-dialog/add-widget-dialog.component';
import { FooterComponent } from './components/footer/footer.component';
import { TvManagementDialogComponent } from './components/header/components/tv-management-dialog/tv-management-dialog.component';
import { SharedModule } from '../shared/shared.module';
import { HeaderComponent } from './components/header/header.component';
import { SidenavComponent } from './components/sidenav/sidenav.component';
import { MenuComponent } from './components/menu/menu.component';

@NgModule({
  imports: [SharedModule],
  declarations: [AddWidgetDialogComponent, FooterComponent, HeaderComponent, SidenavComponent, TvManagementDialogComponent, MenuComponent],
  entryComponents: [AddWidgetDialogComponent, TvManagementDialogComponent],
  exports: [AddWidgetDialogComponent, FooterComponent, HeaderComponent, SidenavComponent]
})
export class LayoutModule {}
