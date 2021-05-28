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

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {DashboardService} from '../../services/dashboard/dashboard.service';
import {Project} from '../../../shared/models/backend/project/project';
import {HttpProjectService} from '../../../shared/services/backend/http-project/http-project.service';
import {ProjectWidget} from '../../../shared/models/backend/project-widget/project-widget';
import {FileUtils} from '../../../shared/utils/file.utils';
import {HeaderConfiguration} from '../../../shared/models/frontend/header/header-configuration';
import {IconEnum} from '../../../shared/enums/icon.enum';
import {HttpScreenService} from '../../../shared/services/backend/http-screen/http-screen.service';
import {ToastTypeEnum} from '../../../shared/enums/toast-type.enum';
import {ToastService} from '../../../shared/services/frontend/toast/toast.service';
import {SidenavService} from '../../../shared/services/frontend/sidenav/sidenav.service';
import {DialogService} from '../../../shared/services/frontend/dialog/dialog.service';
import {TranslateService} from '@ngx-translate/core';
import {ProjectRequest} from '../../../shared/models/backend/project/project-request';
import {ProjectFormFieldsService} from '../../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import {flatMap, switchMap, tap} from 'rxjs/operators';
import {EMPTY, Observable, of} from 'rxjs';
import {DashboardScreenComponent} from '../dashboard-screen/dashboard-screen.component';
import {MatDialog} from '@angular/material/dialog';
import {TvManagementDialogComponent} from '../tv-management-dialog/tv-management-dialog.component';
import {MaterialIconRecords} from '../../../shared/records/material-icon.record';
import {ValueChangedEvent} from '../../../shared/models/frontend/form/value-changed-event';
import {FormField} from '../../../shared/models/frontend/form/form-field';
import {ProjectUsersFormFieldsService} from '../../../shared/services/frontend/form-fields/project-users-form-fields/project-users-form-fields.service';
import {WebsocketService} from '../../../shared/services/frontend/websocket/websocket.service';
import {ImageUtils} from '../../../shared/utils/image.utils';

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
   */
  public dashboardScreen: DashboardScreenComponent;

  /**
   * Hold the configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The token of the dashboard
   */
  public dashboardToken: string;

  /**
   * The project used to display the dashboard
   */
  public project: Project;

  /**
   * The list of widget instance of this project
   */
  public projectWidgets: ProjectWidget[];

  /**
   * True if the dashboard should be displayed readonly, false otherwise
   */
  public isReadOnly = true;

  /**
   * The screen code of the client;
   */
  public screenCode = DashboardService.generateScreenCode();

  /**
   * Used to know if the dashboard is loading
   */
  public isDashboardLoading = true;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
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
    this.dashboardToken = this.activatedRoute.snapshot.params['dashboardToken'];

    this.websocketService.startConnection();

    this.refreshProject()
      .pipe(
        flatMap(() => this.isReadOnlyDashboard()),
        flatMap(() => this.refreshProjectWidgets())
      )
      .subscribe(
        () => {
          this.isDashboardLoading = false;
          this.initHeaderConfiguration();
        },
        () => (this.isDashboardLoading = false)
      );
  }

  /**
   * Refresh the project
   */
  private refreshProject(): Observable<Project> {
    return this.httpProjectService.getById(this.dashboardToken).pipe(tap((project: Project) => (this.project = project)));
  }

  /**
   * Check if the dashboard should be displayed as readonly
   */
  private isReadOnlyDashboard(): Observable<boolean> {
    return this.dashboardService.shouldDisplayedReadOnly(this.dashboardToken).pipe(tap((isReadonly: boolean) => (this.isReadOnly = isReadonly)));
  }

  /**
   * Activate the action of refresh project widgets
   */
  public refreshProjectWidgetsAction(): void {
    this.refreshProjectWidgets().subscribe();
  }

  /**
   * Refresh the project widget list
   */
  private refreshProjectWidgets(): Observable<ProjectWidget[]> {
    return this.httpProjectService
      .getProjectProjectWidgets(this.dashboardToken)
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
          hidden: () => this.isReadOnly,
          callback: () => this.displayProjectWidgetWizard()
        },
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'dashboard.edit' },
          hidden: () => this.isReadOnly,
          callback: () => this.openDashboardFormSidenav()
        },
        {
          icon: IconEnum.USERS,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'user.edit' },
          hidden: () => this.isReadOnly,
          callback: () => this.openUserFormSidenav()
        },
        {
          icon: IconEnum.REFRESH,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.refresh' },
          hidden: () => this.isReadOnly || !this.projectWidgets || this.projectWidgets.length === 0,
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
          hidden: () => this.isReadOnly || !this.projectWidgets || this.projectWidgets.length === 0,
          callback: () => this.openScreenManagementDialog()
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          variant: 'miniFab',
          tooltip: { message: 'dashboard.delete' },
          hidden: () => this.isReadOnly,
          callback: () => this.deleteDashboard()
        }
      ]
    };
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
      belongingComponent: this.dashboardScreen,
      save: (formData: ProjectRequest) => this.editDashboard(formData)
    });
  }

  /**
   * Execute the action edit the dashboard when the sidenav as been saved
   *
   * @param formData The data retrieve from the form sidenav
   */
  private editDashboard(formData: ProjectRequest): void {
    console.warn(formData);
    formData.cssStyle = `.grid { background-color: ${formData['gridBackgroundColor']}; }`;

    this.httpProjectService.update(this.project.token, formData).subscribe(() => {
      if (formData.image) {
        const contentType: string = ImageUtils.getContentTypeFromBase64URL(formData.image);
        const blob: Blob = FileUtils.base64ToBlob(
          ImageUtils.getDataFromBase64URL(formData.image),
          ImageUtils.getContentTypeFromBase64URL(formData.image)
        );
        const file: File = FileUtils.convertBlobToFile(blob, `${this.project.token}.${contentType.split('/')[1]}`, new Date());

        this.httpProjectService.addOrUpdateProjectScreenshot(this.project.token, file).subscribe();
      }

      if (!this.projectWidgets) {
        this.refreshProject().subscribe(() => {
          this.initHeaderConfiguration();
          this.toastService.sendMessage('Dashboard updated', ToastTypeEnum.SUCCESS);
        });
      } else {
        this.refreshConnectedScreens();
      }
    });
  }

  /**
   * Refresh every connected dashboards
   */
  private refreshConnectedScreens(): void {
    this.httpScreenService.refreshEveryConnectedScreensForProject(this.project.token).subscribe();
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

  /**
   *
   * @param content
   */
  @ViewChild('dashboardScreen', { read: ElementRef })
  public set content(content: DashboardScreenComponent) {
    if (content) {
      this.dashboardScreen = content;
    }
  }
}
