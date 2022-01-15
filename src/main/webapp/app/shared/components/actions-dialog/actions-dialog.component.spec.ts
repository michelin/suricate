import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionsDialogComponent } from './actions-dialog.component';
import { MockModule } from '../../../mock/mock.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';

describe('ActionsDialogComponent', () => {
  let component: ActionsDialogComponent;
  let fixture: ComponentFixture<ActionsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [ActionsDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: { data: buildActionsDialogConfiguration() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActionsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

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
      actions: []
    };
  }
});
