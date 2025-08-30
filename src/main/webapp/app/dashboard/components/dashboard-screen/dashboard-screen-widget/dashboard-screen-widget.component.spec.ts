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

import { WidgetStateEnum } from '../../../../shared/enums/widget-sate.enum';
import { ProjectWidget } from '../../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetPosition } from '../../../../shared/models/backend/project-widget/project-widget-position';
import { DashboardScreenWidgetComponent } from './dashboard-screen-widget.component';

describe('DashboardScreenWidgetComponent', () => {
	let component: DashboardScreenWidgetComponent;
	let fixture: ComponentFixture<DashboardScreenWidgetComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [DashboardScreenWidgetComponent],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
				provideTranslateService({
					loader: provideTranslateHttpLoader({ prefix: './assets/i18n/', suffix: '.json' })
				})
			]
		}).compileComponents();

		fixture = TestBed.createComponent(DashboardScreenWidgetComponent);
		component = fixture.componentInstance;
		component.projectWidget = buildMockedProjectWidget();

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	function buildMockedProjectWidget(): ProjectWidget {
		const widgetPosition: ProjectWidgetPosition = {
			gridColumn: 1,
			gridRow: 1,
			width: 200,
			height: 200
		};

		return {
			id: 1,
			data: 'Data',
			widgetPosition: widgetPosition,
			customStyle: '',
			instantiateHtml: '',
			backendConfig: '',
			log: '',
			lastExecutionDate: '',
			lastSuccessDate: '',
			globalConfigOverridden: true,
			state: WidgetStateEnum.RUNNING,
			projectToken: 'Token',
			widgetId: 1,
			gridId: 1
		};
	}
});
