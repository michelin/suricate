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

import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { inject } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs';

import { HttpFilter } from '../../../models/backend/http-filter';
import { PageModel } from '../../../models/backend/page-model';
import { AbstractHttpService } from './abstract-http.service';

describe('AbstractHttpService', () => {
	class TestHttpService extends AbstractHttpService<TestData, TestRequest> {
		private http = inject(HttpClient);

		getAll(filter?: HttpFilter): Observable<PageModel<TestData>> {
			const url = `${AbstractHttpService.baseApiEndpoint}/test`;
			return this.http.get<PageModel<TestData>>(url, { params: filter });
		}

		create(entity: TestRequest): Observable<TestData> {
			const url = `${AbstractHttpService.baseApiEndpoint}/test`;
			return this.http.post<TestData>(url, entity);
		}

		delete(id: number | string): Observable<void> {
			const url = `${AbstractHttpService.baseApiEndpoint}/test/${id}`;
			return this.http.delete<void>(url);
		}

		getById(id: number | string): Observable<TestData> {
			const url = `${AbstractHttpService.baseApiEndpoint}/test/${id}`;
			return this.http.get<TestData>(url);
		}

		update(id: number | string, entity: TestRequest): Observable<void> {
			const url = `${AbstractHttpService.baseApiEndpoint}/test/${id}`;
			return this.http.put<void>(url, entity);
		}
	}

	let service: TestHttpService;
	let httpMock: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [TestHttpService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
		});

		service = TestBed.inject(TestHttpService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
	});

	it('should create', () => {
		expect(service).toBeTruthy();
	});

	it('should call correct endpoint', () => {
		const mockResponse: PageModel<TestData> = {
			content: [{ id: 1, name: 'Test' }],
			page: {
				number: 0,
				size: 10,
				totalElements: 1
			}
		};

		service.getAll().subscribe((response) => {
			expect(response).toEqual(mockResponse);
		});

		const req = httpMock.expectOne(`${AbstractHttpService.baseApiEndpoint}/test`);
		expect(req.request.method).toBe('GET');
		expect(req.request.params.keys().length).toBe(0);

		req.flush(mockResponse);
	});

	it('should pass filter parameters correctly', () => {
		const filter = new HttpFilter();
		filter.search = 'test';
		filter.page = 1;
		filter.size = 20;
		filter.sort = ['name', 'asc'];

		const mockResponse: PageModel<TestData> = {
			content: [],
			page: {
				number: 0,
				size: 20,
				totalElements: 1
			}
		};

		service.getAll(filter).subscribe((response) => {
			expect(response).toEqual(mockResponse);
		});

		const req = httpMock.expectOne((request) => {
			return (
				request.url === `${AbstractHttpService.baseApiEndpoint}/test` &&
				request.params.get('search') === 'test' &&
				request.params.get('page') === '1' &&
				request.params.get('size') === '20' &&
				request.params.getAll('sort').includes('name') &&
				request.params.getAll('sort').includes('asc')
			);
		});

		expect(req.request.method).toBe('GET');
		req.flush(mockResponse);
	});
});

interface TestData {
	id: number;
	name: string;
}

interface TestRequest {
	search?: string;
}
