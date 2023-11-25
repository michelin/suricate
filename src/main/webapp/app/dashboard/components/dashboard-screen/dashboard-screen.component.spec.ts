/*
 * Copyright 2012-2021 the original author or authors.
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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DashboardScreenComponent } from './dashboard-screen.component';
import { SafeHtmlPipe } from '../../../shared/pipes/safe-html/safe-html.pipe';
import { MockedModelBuilderService } from '../../../mock/services/mocked-model-builder/mocked-model-builder.service';
import { MockModule } from '../../../mock/mock.module';

describe('DashboardScreenComponent', () => {
  let component: DashboardScreenComponent;
  let fixture: ComponentFixture<DashboardScreenComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [DashboardScreenComponent, SafeHtmlPipe]
      }).compileComponents();

      const mockedModelBuilderService = TestBed.inject(MockedModelBuilderService);

      fixture = TestBed.createComponent(DashboardScreenComponent);
      component = fixture.componentInstance;
      component.project = mockedModelBuilderService.buildMockedProject();
      component.gridOptions = mockedModelBuilderService.buildGridOptions();

      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
