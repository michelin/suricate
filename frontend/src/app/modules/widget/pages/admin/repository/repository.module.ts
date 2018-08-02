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

import {SharedModule} from '../../../../../shared/shared.module';
import {LayoutModule} from '../../../../../layout/layout.module';

import {RepositoryListComponent} from './repository-list/repository-list.component';
import {RepositoryService} from './repository.service';
import {RouterModule} from '@angular/router';
import {repositoryRoutes} from './repository.route';
import {RepositoryAddEditComponent} from './repository-add-edit/repository-add-edit.component';

@NgModule({
  imports: [
    CommonModule,
    LayoutModule,
    SharedModule,
    RouterModule.forChild(repositoryRoutes)
  ],
  declarations: [
    RepositoryListComponent,
    RepositoryAddEditComponent
  ],
  providers: [
    RepositoryService
  ]
})
export class RepositoryModule {
}
