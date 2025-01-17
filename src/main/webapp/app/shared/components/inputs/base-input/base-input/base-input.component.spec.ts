import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BaseInputComponent } from './base-input.component';
import { MockModule } from '../../../../../mock/mock.module';
import { CheckboxComponent } from '../../checkbox/checkbox.component';

describe('BaseInputComponent', () => {
  let component: BaseInputComponent;
  let fixture: ComponentFixture<BaseInputComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [MockModule, BaseInputComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(BaseInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
