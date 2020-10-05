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

import { DashboardScreenComponent } from './dashboard-screen.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StompRService } from '@stomp/ng2-stompjs';
import { SafeHtmlPipe } from '../../../shared/pipes/safe-html/safe-html.pipe';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project/project-grid';
import { MockUnitTestsService } from '../../../mock/services/mock-unit-tests/mock-unit-tests.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MockModule } from '../../../mock/mock.module';

describe('DashboardScreenComponent', () => {
  let component: DashboardScreenComponent;
  let fixture: ComponentFixture<DashboardScreenComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [DashboardScreenComponent, SafeHtmlPipe]
    }).compileComponents();

    const mockUnitTestsService = TestBed.inject(MockUnitTestsService);

    fixture = TestBed.createComponent(DashboardScreenComponent);
    component = fixture.componentInstance;
    component.project = mockUnitTestsService.buildMockedProject();

    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
