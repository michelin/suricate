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

import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { GridProperties } from '../../../shared/models/backend/project/grid-properties';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project-grid/project-grid';
import { GridOptions } from '../../../shared/models/frontend/grid/grid-options';
import { DashboardScreenComponent } from './dashboard-screen.component';

describe('DashboardScreenComponent', () => {
	let component: DashboardScreenComponent;
	let fixture: ComponentFixture<DashboardScreenComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [DashboardScreenComponent],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
				provideTranslateService({
					loader: provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
				})
			]
		}).compileComponents();

		fixture = TestBed.createComponent(DashboardScreenComponent);
		component = fixture.componentInstance;
		component.project = buildMockedProject();
		component.gridOptions = buildGridOptions();

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	function buildMockedProject(): Project {
		const gridProperties: GridProperties = {
			maxColumn: 5,
			widgetHeight: 300,
			cssStyle: ''
		};

		const grid: ProjectGrid = {
			id: 1,
			time: 30
		};

		return {
			gridProperties: gridProperties,
			librariesToken: ['Token1', 'Token2'],
			name: 'ProjectName',
			screenshotToken: 'ScreenToken',
			image: {
				content: 'content',
				contentType: 'image/png',
				id: 'id',
				lastUpdateDate: new Date(),
				size: 10
			},
			token: 'Token',
			displayProgressBar: false,
			grids: [grid]
		};
	}

	function buildGridOptions(): GridOptions {
		return {
			cols: 5,
			rowHeight: 300,
			gap: 10,
			draggable: true,
			resizable: true,
			compactType: 'vertical'
		};
	}
});
