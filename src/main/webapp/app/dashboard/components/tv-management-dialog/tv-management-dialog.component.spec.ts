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

import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { GridProperties } from '../../../shared/models/backend/project/grid-properties';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project-grid/project-grid';
import { TvManagementDialogComponent } from './tv-management-dialog.component';

describe('TvManagementDialogComponent', () => {
  let component: TvManagementDialogComponent;
  let fixture: ComponentFixture<TvManagementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TvManagementDialogComponent,
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
        { provide: MAT_DIALOG_DATA, useValue: { project: buildProject() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TvManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function buildProject(): Project {
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
});
