import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { IconEnum } from '../../enums/icon.enum';

@Component({
  templateUrl: './actions-dialog.component.html',
  styleUrls: ['./actions-dialog.component.scss']
})
export class ActionsDialogComponent implements OnInit {
  /**
   * The configuration of the confirmation dialog
   */
  public configuration: ActionsDialogConfiguration;

  /**
   * Constructor
   *
   * @param data The data given to the dialog
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: ActionsDialogConfiguration) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.configuration = this.data;
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
