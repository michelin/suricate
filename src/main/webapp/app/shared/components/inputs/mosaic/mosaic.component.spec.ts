/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MosaicComponent } from './mosaic.component';
import { MockUnitTestsService } from '../../../../mock/services/mock-unit-tests/mock-unit-tests.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { MockModule } from '../../../../mock/mock.module';

describe('MosaicComponent', () => {
  let component: MosaicComponent;
  let fixture: ComponentFixture<MosaicComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [MosaicComponent]
    }).compileComponents();

    const mockUnitTestsService = TestBed.inject(MockUnitTestsService);

    fixture = TestBed.createComponent(MosaicComponent);
    component = fixture.componentInstance;
    component.field = mockUnitTestsService.buildMockedFormField(DataTypeEnum.MOSAIC);

    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
