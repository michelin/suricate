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
import { RouterModule } from '@angular/router';
import { LayoutModule } from '../layout/layout.module';
import { SharedModule } from '../shared/shared.module';
import { adminRoutes } from './admin.route';
import { UsersComponent } from './components-list/users/users.component';
import { RepositoriesComponent } from './components-list/repositories/repositories.component';

@NgModule({
  imports: [RouterModule.forChild(adminRoutes), LayoutModule, SharedModule],
  declarations: [UsersComponent, RepositoriesComponent],
  exports: [RouterModule]
})
export class AdminModule {}
