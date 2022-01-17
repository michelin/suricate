import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { IconEnum } from '../../enums/icon.enum';

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
    this.initCloseButtonConfiguration();
  }

  /**
   * Init the buttons configurations
   */
  private initCloseButtonConfiguration(): void {
    const closeButton: ButtonConfiguration<any> = {
      label: 'close',
      icon: IconEnum.CLOSE,
      color: 'primary'
    };

    this.configuration.actions = [closeButton].concat(this.configuration.actions);
  }
}
