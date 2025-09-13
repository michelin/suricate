import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseInput } from './base-input';

describe('BaseInput', () => {
	let component: BaseInput;
	let fixture: ComponentFixture<BaseInput>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [BaseInput]
		}).compileComponents();

		fixture = TestBed.createComponent(BaseInput);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
