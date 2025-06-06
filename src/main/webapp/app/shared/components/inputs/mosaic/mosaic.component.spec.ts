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

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTypeEnum } from '../../../enums/data-type.enum';
import { FormField } from '../../../models/frontend/form/form-field';
import { MosaicComponent } from './mosaic.component';

describe('MosaicComponent', () => {
  let component: MosaicComponent;
  let fixture: ComponentFixture<MosaicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MosaicComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MosaicComponent);
    component = fixture.componentInstance;
    component.field = buildMockedFormField(DataTypeEnum.MOSAIC);

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
});
