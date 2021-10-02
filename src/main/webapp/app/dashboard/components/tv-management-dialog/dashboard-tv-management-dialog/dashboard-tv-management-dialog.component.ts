import {Component, Inject, Injector, OnInit} from '@angular/core';
import {TvManagementDialogComponent} from "../tv-management-dialog.component";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Project} from "../../../../shared/models/backend/project/project";
import {HttpProjectService} from "../../../../shared/services/backend/http-project/http-project.service";

@Component({
  templateUrl: '../tv-management-dialog.component.html',
  styleUrls: ['../tv-management-dialog.component.scss']
})
export class DashboardTvManagementDialogComponent extends TvManagementDialogComponent implements OnInit {
  /**
   * The current project
   */
  public project: Project;

  /**
   * Constructor
   *
   * @param data               The mat dialog data
   * @param injector           The injector
   * @param httpProjectService The HTTP project service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private readonly data: { project: Project },
              protected injector: Injector,
              private readonly httpProjectService: HttpProjectService) {
    super(injector);
  }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.project = this.data.project;
    this.getConnectedWebsocketClient();

    super.ngOnInit();
  }

  /**
   * Retrieve the websocket connections to a dashboard
   */
  public getConnectedWebsocketClient(): void {
    this.httpProjectService.getProjectWebsocketClients(this.project.token).subscribe(websocketClients => {
      this.websocketClients = websocketClients;
    });
  }

  /**
   * Register a screen
   */
  public registerScreen(): void {
    if (this.registerScreenCodeFormField.valid) {
      const screenCode: string = this.registerScreenCodeFormField.get('screenCode').value;

      this.httpScreenService.connectProjectToScreen(this.project.token, +screenCode).subscribe(() => {
        this.registerScreenCodeFormField.reset();
        setTimeout(() => this.getConnectedWebsocketClient(), 2000);
      });
    }
  }

  /**
   * Display the screen code on every connected screens
   */
  public displayScreenCode(): void {
    if (this.project.token) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForProject(this.project.token).subscribe();
    }
  }
}
