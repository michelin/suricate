import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionsDialogComponent } from './actions-dialog.component';
import { MockModule } from '../../../mock/mock.module';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { IconEnum } from '../../enums/icon.enum';

describe('ActionsDialogComponent', () => {
  let component: ActionsDialogComponent;
  let fixture: ComponentFixture<ActionsDialogComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [ActionsDialogComponent],
        providers: [{ provide: MAT_DIALOG_DATA, useValue: buildActionsDialogConfiguration() }]
      }).compileComponents();

      fixture = TestBed.createComponent(ActionsDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Build a mocked ActionsDialogConfiguration for the unit tests
   */
  function buildActionsDialogConfiguration(): ActionsDialogConfiguration {
    return {
      title: 'Title',
      message: 'Message',
      actions: [
        {
          icon: IconEnum.ADD,
          color: 'primary',
          variant: 'miniFab'
        }
      ]
    };
  }
});
