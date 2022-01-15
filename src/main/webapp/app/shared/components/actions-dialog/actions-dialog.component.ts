import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';

@Component({
  templateUrl: './actions-dialog.component.html',
  styleUrls: ['./actions-dialog.component.scss']
})
export class ActionsDialogComponent {
  /**
   * The configuration of the confirmation dialog
   */
  public configuration: ActionsDialogConfiguration;

  /**
   * Constructor
   *
   * @param confirmationDialogRef Reference on the instance of this dialog
   * @param data The data given to the dialog
   */
  constructor(
    private readonly confirmationDialogRef: MatDialogRef<ActionsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: ActionsDialogConfiguration
  ) {
    this.configuration = data;
  }

  /**
   * Call the function when the user click an action
   */
  public doAction(event: Event, action: ButtonConfiguration<unknown>): void {
    this.confirmationDialogRef.close();
    action.callback(event);
  }
}
