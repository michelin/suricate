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

import { WizardComponent } from './wizard.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MockedModelBuilderService } from '../../../mock/services/mocked-model-builder/mocked-model-builder.service';
import { MockModule } from '../../../mock/mock.module';

describe('WizardComponent', () => {
  let component: WizardComponent;
  let fixture: ComponentFixture<WizardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [WizardComponent]
    }).compileComponents();

    const mockedModelBuilderService = TestBed.inject(MockedModelBuilderService);

    fixture = TestBed.createComponent(WizardComponent);
    component = fixture.componentInstance;
    component.wizardConfiguration = mockedModelBuilderService.buildWizardConfiguration();

    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
