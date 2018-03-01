import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDashboardDialogComponent } from './add-dashboard-dialog.component';

describe('AddDashboardDialogComponent', () => {
  let component: AddDashboardDialogComponent;
  let fixture: ComponentFixture<AddDashboardDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddDashboardDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDashboardDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
