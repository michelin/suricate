/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardScreenWidgetComponent } from './dashboard-screen-widget.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  DEFAULT_LANGUAGE,
  MissingTranslationHandler,
  TranslateCompiler,
  TranslateLoader,
  TranslateModule,
  TranslateParser,
  TranslateService,
  TranslateStore,
  USE_DEFAULT_LANG,
  USE_EXTEND,
  USE_STORE
} from '@ngx-translate/core';
import { StompRService } from '@stomp/ng2-stompjs';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProjectWidget } from '../../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetPosition } from '../../../../shared/models/backend/project-widget/project-widget-position';
import { WidgetStateEnum } from '../../../../shared/enums/widget-sate.enum';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MockModule } from '../../../../mock/mock.module';
import { SafeHtmlPipe } from '../../../../shared/pipes/safe-html/safe-html.pipe';

describe('DashboardScreenWidgetComponent', () => {
  let component: DashboardScreenWidgetComponent;
  let fixture: ComponentFixture<DashboardScreenWidgetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [DashboardScreenWidgetComponent, SafeHtmlPipe]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardScreenWidgetComponent);
    component = fixture.componentInstance;
    component.projectWidget = buildMockedProjectWidget();
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked project widget for the unit tests
   */
  function buildMockedProjectWidget(): ProjectWidget {
    const widgetPosition: ProjectWidgetPosition = {
      col: 1,
      row: 1,
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
      widgetId: 1
    };
  }
});
