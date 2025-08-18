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

import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { appRoutes } from '../../../app.routes';
import { IconEnum } from '../../enums/icon.enum';
import { FormStep } from '../../models/frontend/form/form-step';
import { WizardConfiguration } from '../../models/frontend/wizard/wizard-configuration';
import { WizardComponent } from './wizard.component';

describe('WizardComponent', () => {
  let component: WizardComponent;
  let fixture: ComponentFixture<WizardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WizardComponent],
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideRouter(appRoutes),
        provideTranslateService({
          loader: provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WizardComponent);
    component = fixture.componentInstance;
    component.wizardConfiguration = buildWizardConfiguration();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked WizardConfiguration for the unit tests
   */
  function buildWizardConfiguration(): WizardConfiguration {
    const formSteps: FormStep[] = [];

    for (let i = 0; i < 3; i++) {
      formSteps.push({
        key: 'Key' + i,
        title: 'Title' + i,
        icon: IconEnum.ADD
      });
    }

    return {
      steps: formSteps
    };
  }
});
