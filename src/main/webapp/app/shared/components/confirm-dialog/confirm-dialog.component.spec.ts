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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { MockModule } from '../../../mock/mock.module';
import { CommunicationDialogConfiguration } from '../../models/frontend/dialog/communication-dialog-configuration';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { CommunicationDialogComponent } from '../communication-dialog/communication-dialog.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ConfirmDialogComponent,
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
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: buildCommunicationDialogConfiguration() }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked CommunicationDialogConfiguration for the unit tests
   */
  function buildCommunicationDialogConfiguration(): CommunicationDialogConfiguration {
    return {
      title: 'Title',
      message: 'Message',
      isErrorMessage: true
    };
  }
});
