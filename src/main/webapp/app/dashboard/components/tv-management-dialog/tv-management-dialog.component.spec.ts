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

import { ComponentFixture, inject, TestBed, waitForAsync } from '@angular/core/testing';

import { TvManagementDialogComponent } from './tv-management-dialog.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateLoader, TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project/project-grid';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MockModule } from '../../../mock/mock.module';
import { AbstractHttpService } from '../../../shared/services/backend/abstract-http/abstract-http.service';

describe('TvManagementDialogComponent', () => {
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [TvManagementDialogComponent]
      }).compileComponents();
    })
  );

  it('should create', inject([AbstractHttpService], (component: TvManagementDialogComponent) => {
    expect(component).toBeTruthy();
  }));
});
