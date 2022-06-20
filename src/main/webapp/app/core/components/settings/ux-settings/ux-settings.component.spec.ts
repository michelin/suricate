import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UxSettingsComponent } from './ux-settings.component';
import { MockModule } from '../../../../mock/mock.module';

describe('UxSettingsComponent', () => {
  let component: UxSettingsComponent;
  let fixture: ComponentFixture<UxSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [UxSettingsComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UxSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
