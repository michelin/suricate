import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardTvManagementDialogComponent } from './dashboard-tv-management-dialog.component';

describe('DashboardTvManagementDialogComponent', () => {
  let component: DashboardTvManagementDialogComponent;
  let fixture: ComponentFixture<DashboardTvManagementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DashboardTvManagementDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardTvManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
