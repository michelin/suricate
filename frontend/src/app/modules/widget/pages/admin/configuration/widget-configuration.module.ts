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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../../../shared/shared.module';

import {WidgetConfigurationListComponent} from './configuration-list/widget-configuration-list.component';
import {WidgetConfigurationService} from './widget-configuration.service';
import {WidgetConfigurationEditComponent} from './configuration-edit/widget-configuration-edit.component';
import {DeleteWidgetConfigurationDialogComponent} from '../../../components/delete-configuration-dialog/delete-widget-configuration-dialog.component';
import {LayoutModule} from "../../../../../layout/layout.module";
import {widgetConfigurationRoute} from "./widget-configuration.route";

@NgModule({
  imports: [
    CommonModule,
    LayoutModule,
    SharedModule,
    RouterModule.forChild(widgetConfigurationRoute)
  ],
  declarations: [
    WidgetConfigurationListComponent,
    WidgetConfigurationEditComponent,
    DeleteWidgetConfigurationDialogComponent
  ],
  providers: [
    WidgetConfigurationService
  ],
  entryComponents: [
    DeleteWidgetConfigurationDialogComponent
  ]
})
export class WidgetConfigurationModule {
}
