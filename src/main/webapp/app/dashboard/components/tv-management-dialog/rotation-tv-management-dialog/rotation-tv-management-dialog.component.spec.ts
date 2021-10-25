import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RotationTvManagementDialogComponent } from './rotation-tv-management-dialog.component';
import { MockModule } from '../../../../mock/mock.module';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Project } from '../../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../../shared/models/backend/project/project-grid';
import { Rotation } from '../../../../shared/models/backend/rotation/rotation';

describe('RotationTvManagementDialogComponent', () => {
  let component: RotationTvManagementDialogComponent;
  let fixture: ComponentFixture<RotationTvManagementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [RotationTvManagementDialogComponent],
      providers: [{ provide: MAT_DIALOG_DATA, useValue: { rotation: buildMockedRotation() } }]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationTvManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked rotation object for the unit tests
   */
  function buildMockedRotation(): Rotation {
    return {
      name: 'Rotation 1',
      token: 'Token',
      progressBar: false
    };
  }
});
