import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RotationDashboardsSelectionComponent } from './rotation-dashboards-selection.component';
import { MockModule } from '../../../mock/mock.module';

describe('RotationDashboardsSelectionComponent', () => {
  let component: RotationDashboardsSelectionComponent;
  let fixture: ComponentFixture<RotationDashboardsSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
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
