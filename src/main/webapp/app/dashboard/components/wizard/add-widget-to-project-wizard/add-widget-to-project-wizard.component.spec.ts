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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MockModule } from '../../../../mock/mock.module';
import { AddWidgetToProjectWizardComponent } from './add-widget-to-project-wizard.component';
import { DashboardTvComponent } from '../../dashboard-tv/dashboard-tv.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { appRoutes } from '../../../../app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

describe('ProjectWidgetWizardComponent', () => {
  let component: AddWidgetToProjectWizardComponent;
  let fixture: ComponentFixture<AddWidgetToProjectWizardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AddWidgetToProjectWizardComponent,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (httpClient: HttpClient) => new TranslateHttpLoader(httpClient, './assets/i18n/', '.json'),
            deps: [HttpClient]
          }
        })
      ],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideAnimationsAsync(),
        provideRouter(appRoutes)
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddWidgetToProjectWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
