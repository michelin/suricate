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
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { MockModule } from '../../../mock/mock.module';
import { IconEnum } from '../../enums/icon.enum';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { ActionsDialogComponent } from './actions-dialog.component';
import {
  TvManagementDialogComponent
} from '../../../dashboard/components/tv-management-dialog/tv-management-dialog.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

describe('ActionsDialogComponent', () => {
  let component: ActionsDialogComponent;
  let fixture: ComponentFixture<ActionsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ActionsDialogComponent,
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
        { provide: MAT_DIALOG_DATA, useValue: buildActionsDialogConfiguration() }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActionsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked ActionsDialogConfiguration for the unit tests
   */
  function buildActionsDialogConfiguration(): ActionsDialogConfiguration {
    return {
      title: 'Title',
      message: 'Message',
      actions: [
        {
          icon: IconEnum.ADD,
          variant: 'miniFab'
        }
      ]
    };
  }
});
