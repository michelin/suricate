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
import {Routes} from "@angular/router";
import {AuthGuard} from "../../../../../shared/auth/guards/auth.guard";
import {AdminGuard} from "../../../../../shared/auth/guards/admin.guard";
import {WidgetConfigurationEditComponent} from "./configuration-edit/widget-configuration-edit.component";
import {WidgetConfigurationListComponent} from "./configuration-list/widget-configuration-list.component";

export const widgetConfigurationRoute: Routes = [
    {path: 'widgets/admin/configurations', component: WidgetConfigurationListComponent, canActivate: [AuthGuard, AdminGuard]},
    {path: 'widgets/admin/configurations/:configurationKey/edit', component: WidgetConfigurationEditComponent, canActivate: [AuthGuard, AdminGuard]}
];