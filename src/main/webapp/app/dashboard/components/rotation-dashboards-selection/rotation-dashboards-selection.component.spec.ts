import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RotationDashboardsSelectionComponent } from './rotation-dashboards-selection.component';

describe('RotationCreationComponent', () => {
  let component: RotationDashboardsSelectionComponent;
  let fixture: ComponentFixture<RotationDashboardsSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RotationDashboardsSelectionComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationDashboardsSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
