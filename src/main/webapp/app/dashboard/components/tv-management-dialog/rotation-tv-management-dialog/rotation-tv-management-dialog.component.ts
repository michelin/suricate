import {Component, Inject, Injector, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Project} from "../../../../shared/models/backend/project/project";
import {Rotation} from "../../../../shared/models/backend/rotation/rotation";
import {TvManagementDialogComponent} from "../tv-management-dialog.component";

@Component({
  templateUrl: '../tv-management-dialog.component.html',
  styleUrls: ['../tv-management-dialog.component.scss']
})
export class RotationTvManagementDialogComponent extends TvManagementDialogComponent implements OnInit {
  /**
   * The current project
   */
  public rotation: Rotation;

  /**
   * Constructor
   *
   * @param data               The mat dialog data
   * @param injector           The injector
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: { rotation: Rotation },
              protected injector: Injector) {
    super(injector);
  }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.rotation = this.data.rotation;

    super.ngOnInit();
  }

  /**
   * Register a screen
   */
  public registerScreen(): void {
    if (this.registerScreenCodeFormField.valid) {
      const screenCode: string = this.registerScreenCodeFormField.get('screenCode').value;

      this.httpScreenService.connectRotationToScreen(this.rotation.token, +screenCode).subscribe(() => {
        this.registerScreenCodeFormField.reset();
        setTimeout(() => this.getConnectedWebsocketClient(), 2000);
      });
    }
  }

  getConnectedWebsocketClient(): void {
  }
}
