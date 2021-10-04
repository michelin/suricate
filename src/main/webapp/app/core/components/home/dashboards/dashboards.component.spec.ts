import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardsComponent } from './dashboards.component';
import { MockModule } from '../../../../mock/mock.module';

describe('DashboardsComponent', () => {
  let component: DashboardsComponent;
  let fixture: ComponentFixture<DashboardsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [DashboardsComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
