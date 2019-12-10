import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSidenavComponent } from '../../components/form-sidenav/form-sidenav.component';

describe('FormSidenavComponent', () => {
  let component: FormSidenavComponent;
  let fixture: ComponentFixture<FormSidenavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormSidenavComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
