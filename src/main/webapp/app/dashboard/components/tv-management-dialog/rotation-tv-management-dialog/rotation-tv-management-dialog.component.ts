import { Component, Inject, Injector, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Rotation } from '../../../../shared/models/backend/rotation/rotation';
import { TvManagementDialogComponent } from '../tv-management-dialog.component';
import { HttpRotationService } from '../../../../shared/services/backend/http-rotation/http-rotation.service';

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
   * @param data                The mat dialog data
   * @param injector            The injector
   * @param httpRotationService The HTTP rotation service
   */
  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly data: { rotation: Rotation },
    protected injector: Injector,
    private readonly httpRotationService: HttpRotationService
  ) {
    super(injector);
  }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.rotation = this.data.rotation;
    this.getConnectedWebsocketClient();

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

  /**
   * Retrieve the websocket connections to a dashboard
   */
  public getConnectedWebsocketClient(): void {
    this.httpRotationService.getRotationWebsocketClients(this.rotation.token).subscribe(websocketClients => {
      this.websocketClients = websocketClients;
    });
  }

  /**
   * Display the screen code on every connected screens
   */
  public displayScreenCode(): void {
    if (this.rotation.token) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForRotation(this.rotation.token).subscribe();
    }
  }
}
