import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardTvManagementDialogComponent } from './dashboard-tv-management-dialog.component';
import { Project } from '../../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../../shared/models/backend/project/project-grid';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MockModule } from '../../../../mock/mock.module';

describe('DashboardTvManagementDialogComponent', () => {
  let component: DashboardTvManagementDialogComponent;
  let fixture: ComponentFixture<DashboardTvManagementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [DashboardTvManagementDialogComponent],
      providers: [{ provide: MAT_DIALOG_DATA, useValue: { project: buildMockedProject() } }]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardTvManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked project object for the unit tests
   */
  function buildMockedProject(): Project {
    const gridProperties: ProjectGrid = {
      maxColumn: 5,
      widgetHeight: 300,
      cssStyle: ''
    };

    return {
      gridProperties: gridProperties,
      librariesToken: ['Token1', 'Token2'],
      name: 'ProjectName',
      screenshotToken: 'ScreenToken',
      image: {
        content: 'content',
        contentType: 'image/png',
        id: 'id',
        lastUpdateDate: new Date(),
        size: 10
      },
      token: 'Token'
    };
  }
});
