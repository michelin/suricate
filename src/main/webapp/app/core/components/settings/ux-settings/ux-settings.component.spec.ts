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

import { UxSettingsComponent } from './ux-settings.component';
import { MockModule } from '../../../../mock/mock.module';
import { AuthenticationService } from '../../../../shared/services/frontend/authentication/authentication.service';

describe('UxSettingsComponent', () => {
  let component: UxSettingsComponent;
  let fixture: ComponentFixture<UxSettingsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [UxSettingsComponent]
      }).compileComponents();

      AuthenticationService.setAccessToken(
        'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzAxNzAyMTg1LCJpYXQiOjE3MDE2MTU3ODUsIm1vZGUiOiJEQVRBQkFTRSIsImZpcnN0bmFtZSI6InRlc3QiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sImVtYWlsIjoidGVzdEB0ZXN0IiwibGFzdG5hbWUiOiJ0ZXN0In0.AXesv1_XVn_mCdSKmK9PEVJC9bE4op6e9oGQN5KVyyY'
      );

      fixture = TestBed.createComponent(UxSettingsComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
