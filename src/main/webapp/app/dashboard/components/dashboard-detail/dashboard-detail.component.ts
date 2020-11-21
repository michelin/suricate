/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as html2canvas from 'html2canvas';

import { DashboardService } from '../../services/dashboard/dashboard.service';
import { Project } from '../../../shared/models/backend/project/project';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { FileUtils } from '../../../shared/utils/file.utils';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { HttpScreenService } from '../../../shared/services/backend/http-screen/http-screen.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { DialogService } from '../../../shared/services/frontend/dialog/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { ProjectFormFieldsService } from '../../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import { flatMap, switchMap, tap } from 'rxjs/operators';
import { EMPTY, Observable, of } from 'rxjs';
import { DashboardScreenComponent } from '../dashboard-screen/dashboard-screen.component';
import { MatDialog } from '@angular/material/dialog';
import { TvManagementDialogComponent } from '../tv-management-dialog/tv-management-dialog.component';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { ValueChangedEvent } from '../../../shared/models/frontend/form/value-changed-event';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ProjectUsersFormFieldsService } from '../../../shared/services/frontend/form-fields/project-users-form-fields/project-users-form-fields.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';

/**
 * Component used to display a specific dashboard
 */
@Component({
  selector: 'suricate-dashboard-detail',
  templateUrl: './dashboard-detail.component.html',
  styleUrls: ['./dashboard-detail.component.scss']
})
export class DashboardDetailComponent implements OnInit {
  /**
   * The dashboard html (as HTML Element)
   * @type {DashboardScreenComponent}
   * @private
   */
  @ViewChild('dashboardScreen')
  private dashboardScreen: DashboardScreenComponent;

  /**
   * Hold the configuration of the header component
   * @type {HeaderConfiguration}
   * @protected
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The project used to display the dashboard
   * @type {Project}
   * @protected
   */
  public project: Project;

  /**
   * The list of widget instance of this project
   * @type {ProjectWidget[]}
   * @protected
   */
  public projectWidgets: ProjectWidget[];

  /**
   * True if the dashboard should be displayed readonly, false otherwise
   * @type {boolean}
   * @protected
   */
  public isReadOnly = true;

  /**
   * The screen code of the client;
   * @type number
   * @protected
   */
  public screenCode = DashboardService.generateScreenCode();

  /**
   * Used to know if the dashboard is loading
   * @type boolean
   * @protected
   */
  public isDashboardLoading = true;

  /**
   * The timer used to take the screenshot
   * @type {NodeJS.Timer}
   * @private
   */
  private screenshotTimer: NodeJS.Timer;

  /**
   * The list of icons
   * @type {IconEnum}
   * @protected
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   * @type {MaterialIconRecords}
   * @protected
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param router Angular service used to manage App's route
   * @param activatedRoute Angular service used to manage the route activated by the component
   * @param matDialog Angular material service used to manage dialog
   * @param translateService NgxTranslate service used to manage translations
   * @param httpProjectService Suricate service used to manage http calls for project
   * @param httpScreenService Suricate service used to manage http calls for screen service
   * @param projectUsersFormFieldsService Frontend service used to generate form fields for projectUsers
   * @param dashboardService Frontend service used to manage dashboards
   * @param sidenavService Frontend service used to manage sidenav
   * @param toastService Frontend service used to manage toast message
   * @param dialogService Frontend service used to manage dialog
   * @param websocketService Frontend service used to manage websockets
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly matDialog: MatDialog,
    private readonly translateService: TranslateService,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpScreenService: HttpScreenService,
    private readonly projectUsersFormFieldsService: ProjectUsersFormFieldsService,
    private readonly dashboardService: DashboardService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService,
    private readonly dialogService: DialogService,
    private readonly websocketService: WebsocketService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    const dashboardToken = this.activatedRoute.snapshot.params['dashboardToken'];

    this.websocketService.startConnection();

    this.refreshProject(dashboardToken)
      .pipe(
        flatMap(() => this.isReadOnlyDashboard(dashboardToken)),
        flatMap(() => this.refreshProjectWidgets(dashboardToken))
      )
      .subscribe(
        () => {
          this.isDashboardLoading = false;
          this.initHeaderConfiguration();
          this.engageDashboardScreenshotsAction();
        },
        () => (this.isDashboardLoading = false)
      );
  }

  /**
   * Refresh the project
   *
   * @param dashboardToken The token used for the refresh
   */
  private refreshProject(dashboardToken: string): Observable<Project> {
    return this.httpProjectService.getById(dashboardToken).pipe(tap((project: Project) => (this.project = project)));
  }

  /**
   * Check if the dashboard should be displayed as readonly
   *
   * @param dashboardToken The dashboard token to check
   */
  private isReadOnlyDashboard(dashboardToken: string): Observable<boolean> {
    return this.dashboardService.shouldDisplayedReadOnly(dashboardToken).pipe(tap((isReadonly: boolean) => (this.isReadOnly = isReadonly)));
  }

  /**
   * Activate the action of refresh project widgets
   */
  public refreshProjectWidgetsAction(): void {
    this.refreshProjectWidgets(this.project.token).subscribe();
  }
  /**
   * Refresh the project widget list
   */
  private refreshProjectWidgets(dashboardToken: string): Observable<ProjectWidget[]> {
    return this.httpProjectService
      .getProjectProjectWidgets(dashboardToken)
      .pipe(tap((projectWidgets: ProjectWidget[]) => (this.projectWidgets = projectWidgets)));
  }

  /**
   * Init the header of the dashboard detail screen
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: this.project.name,
      actions: [
        {
          icon: IconEnum.ADD,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'widget.add' },
          callback: () => this.displayProjectWidgetWizard()
        },
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'dashboard.edit' },
          callback: () => this.openDashboardFormSidenav()
        },
        {
          icon: IconEnum.USERS,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'user.edit' },
          callback: () => this.openUserFormSidenav()
        },
        {
          icon: IconEnum.REFRESH,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.refresh' },
          hidden: () => !this.projectWidgets || this.projectWidgets.length === 0,
          callback: () => this.refreshConnectedScreens()
        },
        {
          icon: IconEnum.TV,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'tv.view' },
          hidden: () => !this.projectWidgets || this.projectWidgets.length === 0,
          callback: () => this.redirectToTvView()
        },
        {
          icon: IconEnum.TV_LIVE,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.management' },
          hidden: () => !this.projectWidgets || this.projectWidgets.length === 0,
          callback: () => this.openScreenManagementDialog()
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          variant: 'miniFab',
          tooltip: { message: 'dashboard.delete' },
          callback: () => this.deleteDashboard()
        }
      ]
    };
  }

  /**
   * Try to engage the action of taking a screenshots
   */
  private engageDashboardScreenshotsAction(): void {
    if (!this.isReadOnly) {
      // We clear the timer so if the user is doing modification, on the dashboard it will not disturbed
      clearTimeout(this.screenshotTimer);

      // We are waiting 10sec before taking the screenshot
      this.screenshotTimer = global.setTimeout(() => this.takeScreenshots(), 10000);
    }
  }

  /**
   * Execute the take screenshots action
   */
  private takeScreenshots(): void {
    // Waiting for behing readonly and take the screenshot
    setTimeout(() => {
      html2canvas(this.dashboardScreen['elementRef'].nativeElement).then((htmlCanvasElement: HTMLCanvasElement) => {
        this.httpProjectService
          .addOrUpdateProjectScreenshot(this.project.token, FileUtils.takeScreenShot(htmlCanvasElement, `${this.project.token}.png`))
          .subscribe();
      });
    }, 0);
  }

  /**
   * Redirect to the wizard used to add a new widget
   */
  public displayProjectWidgetWizard(): void {
    this.router.navigate(['/dashboards', this.project.token, 'widgets', 'create']);
  }

  /**
   * Open the user form sidenav
   */
  private openUserFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'user.add',
      formFields: this.projectUsersFormFieldsService.generateProjectUsersFormFields(this.project.token),
      hideSaveAction: true,
      onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onValueChanged(valueChangedEvent)
    });
  }

  /**
   * Add the selected user to dashboard when values change
   *
   * @param valueChangedEvent The value changed event
   */
  private onValueChanged(valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    if (valueChangedEvent.type === 'optionSelected' && valueChangedEvent.fieldKey === 'usernameAutocomplete') {
      return this.httpProjectService
        .addUserToProject(this.project.token, valueChangedEvent.value)
        .pipe(switchMap(() => of(this.projectUsersFormFieldsService.generateProjectUsersFormFields(this.project.token))));
    }

    return EMPTY;
  }

  /**
   * Open the form sidenav used to edit the dashboard
   */
  private openDashboardFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'dashboard.edit',
      formFields: ProjectFormFieldsService.generateProjectFormFields(this.project),
      save: (formData: ProjectRequest) => this.editDashboard(formData)
    });
  }

  /**
   * Execute the action edit the dashboard when the sidenav as been saved
   *
   * @param formData The data retrieve from the form sidenav
   */
  private editDashboard(formData: ProjectRequest): void {
    formData.cssStyle = `.grid { background-color: ${formData['gridBackgroundColor']}; }`;

    this.httpProjectService.update(this.project.token, formData).subscribe(() => {
      this.toastService.sendMessage('Dashboard updated', ToastTypeEnum.SUCCESS);
      this.refreshConnectedScreens();
    });
  }

  /**
   * Refresh every connected dashboards
   */
  private refreshConnectedScreens(): void {
    this.httpScreenService.refreshEveryConnectedScreensForProject(this.project.token).subscribe(() => {
      this.toastService.sendMessage('Screens refreshed', ToastTypeEnum.SUCCESS);
    });
  }

  /**
   * Open a new tab with the TV view
   */
  private redirectToTvView(): void {
    const url = this.router.createUrlTree(['/tv'], { queryParams: { token: this.project.token } });
    window.open(url.toString(), '_blank');
  }

  /**
   * Open the dialog used to manage screens
   */
  private openScreenManagementDialog(): void {
    this.matDialog.open(TvManagementDialogComponent, {
      role: 'dialog',
      width: '700px',
      maxHeight: '80%',
      autoFocus: false,
      data: { project: this.project }
    });
  }

  /**
   * Delete the current dashboard
   */
  private deleteDashboard(): void {
    this.dialogService.confirm({
      title: 'dashboard.delete',
      message: `${this.translateService.instant('delete.confirm')} ${this.project.name.toUpperCase()} ?`,
      accept: () => {
        this.httpProjectService.delete(this.project.token).subscribe(() => {
          this.toastService.sendMessage('Project deleted successfully', ToastTypeEnum.SUCCESS);
          this.router.navigate(['/home']);
        });
      }
    });
  }

  /**
   * Handle the disconnection of a dashboard
   */
  public handlingDashboardDisconnect(): void {
    this.router.navigate(['/home']);
  }
}
