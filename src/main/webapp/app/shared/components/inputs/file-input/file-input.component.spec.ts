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
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { FileInputComponent } from './file-input.component';
import { CheckboxComponent } from '../checkbox/checkbox.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FormField } from '../../../models/frontend/form/form-field';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

describe('FileInputComponent', () => {
  let component: FileInputComponent;
  let fixture: ComponentFixture<FileInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FileInputComponent,
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

    fixture = TestBed.createComponent(FileInputComponent);
    const formBuilder = TestBed.inject(UntypedFormBuilder);

    component = fixture.componentInstance;
    component.field = buildMockedFormField(DataTypeEnum.FILE);
    component.formGroup = buildMockedFormGroup(DataTypeEnum.FILE, formBuilder);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked FormField for the unit tests
   *
   * @param type The type of the field to create
   */
  function buildMockedFormField(type: DataTypeEnum): FormField {
    return {
      key: 'Key',
      type: type
    };
  }

  /**
   * Build a mocked FormGroup for the unit tests
   *
   * @param type The type of the field to create
   * @param formBuilder The form builder to use to create the form group
   */
  function buildMockedFormGroup(type: DataTypeEnum, formBuilder: UntypedFormBuilder): UntypedFormGroup {
    const customField = buildMockedFormField(type);

    const formGroup: UntypedFormGroup = formBuilder.group({});
    formGroup.addControl(customField.key, new UntypedFormControl(customField.value));

    return formGroup;
  }
});
