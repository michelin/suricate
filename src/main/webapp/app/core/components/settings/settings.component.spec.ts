import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsComponent } from './settings.component';
import { MockModule } from '../../../mock/mock.module';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [SettingsComponent]
    }).compileComponents();

    AuthenticationService.setAccessToken(
      'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNzAxNzAyMTg1LCJpYXQiOjE3MDE2MTU3ODUsIm1vZGUiOiJEQVRBQkFTRSIsImZpcnN0bmFtZSI6InRlc3QiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sImVtYWlsIjoidGVzdEB0ZXN0IiwibGFzdG5hbWUiOiJ0ZXN0In0.AXesv1_XVn_mCdSKmK9PEVJC9bE4op6e9oGQN5KVyyY'
    );
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
