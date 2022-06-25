import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UxSettingsComponent } from './ux-settings.component';
import { MockModule } from '../../../../mock/mock.module';
import { AuthenticationService } from '../../../../shared/services/frontend/authentication/authentication.service';

describe('UxSettingsComponent', () => {
  let component: UxSettingsComponent;
  let fixture: ComponentFixture<UxSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [UxSettingsComponent]
    }).compileComponents();

    AuthenticationService.setAccessToken(
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdG5hbWUiOiJMb2ljIiwibWFpbCI6ImxvaWMuZ3JlZmZpZXJfZXh0QG1pY2hlbGluLmNvbSIsInVzZXJfbmFtZSI6ImZ4MzA2MzIiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNjAxNjUzMjQzLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IjgxNWY4M2VmLWYxMDktNDQ4My1hZGYzLTRhNDBkNzBlMzc5YSIsImNsaWVudF9pZCI6InN1cmljYXRlQW5ndWxhciIsImxhc3RuYW1lIjoiR3JlZmZpZXIifQ.B6C4CckHoOoDZi83dPJaxJZ7-SPaIwT7FNUgwGiKLm0'
    );
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
