/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

import { ElementRef, NgModule } from '@angular/core';
import { MockRxStompService } from './services/mock-rx-stomp/mock-rx-stomp.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';
import { MockElementRef } from './models/mock-element-ref';
import { RxStompService } from '../shared/services/frontend/rx-stomp/rx-stomp.service';
import { CoreModule } from '../core/core.module';

@NgModule({
  imports: [
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (httpClient: HttpClient) => new TranslateHttpLoader(httpClient, './assets/i18n/', '.json'),
        deps: [HttpClient]
      }
    }),
    LayoutModule,
    SharedModule,
    CoreModule,
    HttpClientTestingModule,
    RouterTestingModule
  ],
  providers: [{ provide: RxStompService, useClass: MockRxStompService }, { provide: ElementRef, useClass: MockElementRef }],
  exports: [LayoutModule, SharedModule, HttpClientTestingModule, RouterTestingModule]
})
export class MockModule {}
