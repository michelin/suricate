import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UxSettingsComponent } from './ux-settings.component';

describe('UxSettingsComponent', () => {
  let component: UxSettingsComponent;
  let fixture: ComponentFixture<UxSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UxSettingsComponent ]
    })
    .compileComponents();
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
