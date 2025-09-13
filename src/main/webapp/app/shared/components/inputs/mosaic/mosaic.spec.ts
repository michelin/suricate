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

import { DataType } from '../../../enums/data-type';
import { FormField } from '../../../models/frontend/form/form-field';
import { Mosaic } from './mosaic';

describe('Mosaic', () => {
	let component: Mosaic;
	let fixture: ComponentFixture<Mosaic>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [Mosaic]
		}).compileComponents();

		fixture = TestBed.createComponent(Mosaic);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('field', buildMockedFormField(DataType.MOSAIC));

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
	function buildMockedFormField(type: DataType): FormField {
		return {
			key: 'Key',
			type: type
		};
	}
});
