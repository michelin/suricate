/*
 * Copyright 2012-2021 the original author or authors.
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
import { RouterModule } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { LayoutModule } from '../layout/layout.module';
import { coreRoutes } from './core.route';
import { HomeComponent } from './components/home/home.component';
import { SettingsComponent } from './components/settings/settings.component';
import { UxSettingsComponent } from './components/settings/ux-settings/ux-settings.component';
import { SecuritySettingsComponent } from './components/settings/security-settings/security-settings.component';
import {ClipboardModule} from "@angular/cdk/clipboard";

@NgModule({
  imports: [RouterModule.forChild(coreRoutes), LayoutModule, SharedModule],
  declarations: [LoginComponent, RegisterComponent, HomeComponent, SettingsComponent, UxSettingsComponent, SecuritySettingsComponent]
})
export class CoreModule {}
