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
import { MockedModelBuilderService } from '../../../../mock/services/mocked-model-builder/mocked-model-builder.service';
import { DashboardScreenWidgetComponent } from './dashboard-screen-widget.component';
import { DashboardScreenComponent } from '../dashboard-screen.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Project } from '../../../../shared/models/backend/project/project';
import { GridProperties } from '../../../../shared/models/backend/project/grid-properties';
import { ProjectGrid } from '../../../../shared/models/backend/project-grid/project-grid';
import { ProjectWidget } from '../../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetPosition } from '../../../../shared/models/backend/project-widget/project-widget-position';
import { WidgetStateEnum } from '../../../../shared/enums/widget-sate.enum';

describe('DashboardScreenWidgetComponent', () => {
  let component: DashboardScreenWidgetComponent;
  let fixture: ComponentFixture<DashboardScreenWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DashboardScreenWidgetComponent,
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

    fixture = TestBed.createComponent(DashboardScreenWidgetComponent);
    component = fixture.componentInstance;
    component.projectWidget = buildMockedProjectWidget();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function buildMockedProjectWidget(): ProjectWidget {
    const widgetPosition: ProjectWidgetPosition = {
      gridColumn: 1,
      gridRow: 1,
      width: 200,
      height: 200
    };

    return {
      id: 1,
      data: 'Data',
      widgetPosition: widgetPosition,
      customStyle: '',
      instantiateHtml: '',
      backendConfig: '',
      log: '',
      lastExecutionDate: '',
      lastSuccessDate: '',
      globalConfigOverridden: true,
      state: WidgetStateEnum.RUNNING,
      projectToken: 'Token',
      widgetId: 1,
      gridId: 1
    };
  }
});
