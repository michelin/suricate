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

import { MockModule } from '../../../mock/mock.module';
import { MockedModelBuilderService } from '../../../mock/services/mocked-model-builder/mocked-model-builder.service';
import { SafeHtmlPipe } from '../../../shared/pipes/safe-html/safe-html.pipe';
import { DashboardScreenComponent } from './dashboard-screen.component';
import { DashboardDetailComponent } from '../dashboard-detail/dashboard-detail.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { appRoutes } from '../../../app.routes';
import { Project } from '../../../shared/models/backend/project/project';
import { GridProperties } from '../../../shared/models/backend/project/grid-properties';
import { ProjectGrid } from '../../../shared/models/backend/project-grid/project-grid';
import { GridOptions } from '../../../shared/models/frontend/grid/grid-options';

describe('DashboardScreenComponent', () => {
  let component: DashboardScreenComponent;
  let fixture: ComponentFixture<DashboardScreenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DashboardScreenComponent,
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
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardScreenComponent);
    component = fixture.componentInstance;
    component.project = buildMockedProject();
    component.gridOptions = buildGridOptions();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function buildMockedProject(): Project {
    const gridProperties: GridProperties = {
      maxColumn: 5,
      widgetHeight: 300,
      cssStyle: ''
    };

    const grid: ProjectGrid = {
      id: 1,
      time: 30
    };

    return {
      gridProperties: gridProperties,
      librariesToken: ['Token1', 'Token2'],
      name: 'ProjectName',
      screenshotToken: 'ScreenToken',
      image: {
        content: 'content',
        contentType: 'image/png',
        id: 'id',
        lastUpdateDate: new Date(),
        size: 10
      },
      token: 'Token',
      displayProgressBar: false,
      grids: [grid]
    };
  }

  function buildGridOptions(): GridOptions {
    return {
      cols: 5,
      rowHeight: 300,
      gap: 10,
      draggable: true,
      resizable: true,
      compactType: 'vertical'
    };
  }
});

