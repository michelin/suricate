import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSidenavComponent } from './form-sidenav.component';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MockModule } from '../../../mock/mock.module';

describe('FormSidenavComponent', () => {
  let component: FormSidenavComponent;
  let fixture: ComponentFixture<FormSidenavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [FormSidenavComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormSidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
