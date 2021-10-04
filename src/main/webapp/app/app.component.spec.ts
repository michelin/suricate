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

import { AppComponent } from './app.component';
import { MatDialogModule } from '@angular/material/dialog';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ToastComponent } from './shared/components/toast/toast.component';
import { AuthenticationService } from './shared/services/frontend/authentication/authentication.service';
import { MockModule } from './mock/mock.module';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [AppComponent]
      }).compileComponents();

      AuthenticationService.setAccessToken(
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdG5hbWUiOiJMb2ljIiwibWFpbCI6ImxvaWMuZ3JlZmZpZXJfZXh0QG1pY2hlbGluLmNvbSIsInVzZXJfbmFtZSI6ImZ4MzA2MzIiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNjAxNjUzMjQzLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IjgxNWY4M2VmLWYxMDktNDQ4My1hZGYzLTRhNDBkNzBlMzc5YSIsImNsaWVudF9pZCI6InN1cmljYXRlQW5ndWxhciIsImxhc3RuYW1lIjoiR3JlZmZpZXIifQ.B6C4CckHoOoDZi83dPJaxJZ7-SPaIwT7FNUgwGiKLm0'
      );

      fixture = TestBed.createComponent(AppComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it(
    'should create',
    waitForAsync(() => {
      expect(component).toBeTruthy();
    })
  );
});
