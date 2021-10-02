import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RotationTvManagementDialogComponent } from './rotation-tv-management-dialog.component';

describe('RotationTvManagementDialogComponent', () => {
  let component: RotationTvManagementDialogComponent;
  let fixture: ComponentFixture<RotationTvManagementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RotationTvManagementDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationTvManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
