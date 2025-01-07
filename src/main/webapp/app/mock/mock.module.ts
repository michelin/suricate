/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ElementRef, NgModule } from '@angular/core';
import { MockRxStompService } from './services/mock-rx-stomp/mock-rx-stomp.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';
import { MockElementRef } from './models/mock-element-ref';
import { RxStompService } from '../shared/services/frontend/rx-stomp/rx-stomp.service';
import { CoreModule } from '../core/core.module';

@NgModule({
  exports: [LayoutModule, SharedModule],
  imports: [TranslateModule.forRoot({
    loader: {
      provide: TranslateLoader,
      useFactory: (httpClient: HttpClient) => new TranslateHttpLoader(httpClient, './assets/i18n/', '.json'),
      deps: [HttpClient]
    }
  }),
    LayoutModule,
    SharedModule,
    CoreModule
  ],
  providers: [
    { provide: RxStompService, useClass: MockRxStompService },
    { provide: ElementRef, useClass: MockElementRef },
    provideHttpClient(withInterceptorsFromDi()),
    provideHttpClientTesting()
  ]
})
export class MockModule {}
