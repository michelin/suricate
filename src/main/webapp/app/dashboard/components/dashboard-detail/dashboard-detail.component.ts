/*
 * Copyright 2012-2021 the original author or authors.
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

import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

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
import { flatMap, switchMap, takeUntil, tap } from 'rxjs/operators';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { DashboardScreenComponent } from '../dashboard-screen/dashboard-screen.component';
import { MatDialog } from '@angular/material/dialog';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { ValueChangedEvent } from '../../../shared/models/frontend/form/value-changed-event';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ProjectUsersFormFieldsService } from '../../../shared/services/frontend/form-fields/project-users-form-fields/project-users-form-fields.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { ImageUtils } from '../../../shared/utils/image.utils';
import { TvManagementDialogComponent } from '../tv-management-dialog/tv-management-dialog.component';
import { HttpProjectGridService } from '../../../shared/services/backend/http-project-grid/http-project-grid.service';
import { HttpProjectWidgetService } from '../../../shared/services/backend/http-project-widget/http-project-widget.service';
import { ProjectGridRequest } from '../../../shared/models/backend/project-grid/project-grid-request';
import { ProjectGrid } from '../../../shared/models/backend/project-grid/project-grid';
import { GridRequest } from '../../../shared/models/backend/project-grid/grid-request';

/**
 * Component used to display a specific dashboard
 */
@Component({
  selector: 'suricate-dashboard-detail',
  templateUrl: './dashboard-detail.component.html',
  styleUrls: ['./dashboard-detail.component.scss']
})
export class DashboardDetailComponent implements OnInit, OnDestroy {
  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

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
   * The id of the grid
   */
  public gridId: number;

  /**
   * The project used to display the dashboard
   */
  public project: Project;

  /**
   * All the widgets of the dashboard
   */
  public allWidgets: ProjectWidget[];

  /**
   * Widgets of the current grid
   */
  public currentWidgets: ProjectWidget[];

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
   * @param httpProjectWidgetsService Suricate service used to manage http calls for project
   * @param httpProjectGridsService The HTTP project grids service
   * @param httpScreenService Suricate service used to manage http calls for screen service
   * @param projectUsersFormFieldsService Frontend service used to generate form fields for projectUsers
   * @param dashboardService Frontend service used to manage dashboards
   * @param sidenavService Frontend service used to manage sidenav
   * @param toastService Frontend service used to manage toast message
   * @param dialogService Frontend service used to manage dialog
   * @param websocketService Frontend service used to manage websockets
   * @param projectFormFieldsService Frontend service used to build project form fields
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly matDialog: MatDialog,
    private readonly translateService: TranslateService,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpProjectWidgetsService: HttpProjectWidgetService,
    private readonly httpProjectGridsService: HttpProjectGridService,
    private readonly httpScreenService: HttpScreenService,
    private readonly projectUsersFormFieldsService: ProjectUsersFormFieldsService,
    private readonly dashboardService: DashboardService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService,
    private readonly dialogService: DialogService,
    private readonly websocketService: WebsocketService,
    private readonly projectFormFieldsService: ProjectFormFieldsService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.dashboardToken = this.activatedRoute.snapshot.params['dashboardToken'];

    this.websocketService.startConnection();

    // When gridId parameter changes
    this.activatedRoute.params.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this.gridId = +this.activatedRoute.snapshot.params['gridId'];

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
          () => {
            this.isDashboardLoading = false;
            this.router.navigate(['/home']);
          }
        );
    });
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    this.websocketService.disconnect();
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
    return this.dashboardService
      .shouldDisplayedReadOnly(this.dashboardToken)
      .pipe(tap((isReadonly: boolean) => (this.isReadOnly = isReadonly)));
  }

  /**
   * Activate the action of refresh project widgets
   */
  public refreshAllProjectWidgets(): void {
    this.refreshProjectWidgets().subscribe();
  }

  /**
   * Refresh the project widget list
   */
  private refreshProjectWidgets(): Observable<ProjectWidget[]> {
    return this.httpProjectWidgetsService.getAllByProjectToken(this.dashboardToken).pipe(
      tap((projectWidgets: ProjectWidget[]) => {
        this.allWidgets = projectWidgets;

        if (this.gridId && this.allWidgets) {
          this.currentWidgets = this.allWidgets.filter(widget => widget.gridId === this.gridId);
        }
      })
    );
  }

  /**
   * When manually triggered, rotate to the required dashboard
   *
   * @param gridId The id of the grid to display
   */
  public redirectToGrid(gridId: number): void {
    if (this.gridId !== gridId) {
      this.router.navigate(['/dashboards', this.dashboardToken, gridId]);
    }
  }

  /**
   * Redirect to the add grid page
   */
  public redirectToAddGrid(): void {
    this.router.navigate(['/dashboards', this.dashboardToken]);
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
          hidden: () => this.isReadOnly || !this.gridId,
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
          icon: IconEnum.GRID,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'dashboard.grid.management' },
          hidden: () => this.isReadOnly || this.project.grids.length === 1,
          callback: () => this.openGridsManagementSidenav()
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
          hidden: () => this.isReadOnly || !this.allWidgets || this.allWidgets.length === 0,
          callback: () => this.refreshConnectedScreens()
        },
        {
          icon: IconEnum.TV,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'tv.view' },
          hidden: () => !this.allWidgets || this.allWidgets.length === 0,
          callback: () => this.redirectToTvView()
        },
        {
          icon: IconEnum.TV_LIVE,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.management' },
          hidden: () => this.isReadOnly || !this.allWidgets || this.allWidgets.length === 0,
          callback: () => this.openScreenManagementDialog()
        },
        {
          icon: IconEnum.DELETE_FOREVER,
          color: 'warn',
          variant: 'miniFab',
          tooltip: { message: this.project.grids.length === 1 ? 'dashboard.delete' : 'dashboard.grid.delete' },
          hidden: () => this.isReadOnly,
          callback: () => {
            this.project.grids.length === 1 ? this.deleteDashboard() : this.deleteDashboardOrGrid();
          }
        }
      ]
    };
  }

  /**
   * Redirect to the wizard used to add a new widget
   */
  public displayProjectWidgetWizard(): void {
    this.router.navigate(['/dashboards', this.project.token, this.gridId, 'widgets', 'create']);
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
      formFields: this.projectFormFieldsService.generateProjectFormFields(this.project),
      belongingComponent: this.dashboardScreen,
      save: (formData: ProjectRequest) => this.editDashboard(formData)
    });
  }

  /**
   * Open the form sidenav used to add a grid
   */
  public openAddGridFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'grid.add',
      formFields: this.projectFormFieldsService.generateAddGridFormField(),
      save: (formData: GridRequest) => this.addNewGrid(formData)
    });
  }

  /**
   * Open the grids management sidenav used to edit the grids
   */
  private openGridsManagementSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'dashboard.grid.management',
      formFields: this.projectFormFieldsService.generateGridsManagementFormFields(this.project),
      save: (formData: ProjectGridRequest) => this.editGrids(formData)
    });
  }

  /**
   * Execute the action edit the dashboard when the sidenav has been saved
   *
   * @param formData The data retrieve from the form sidenav
   */
  private editDashboard(formData: ProjectRequest): void {
    formData.cssStyle = `.grid { background-color: ${formData.gridBackgroundColor}; }`;

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

      this.toastService.sendMessage('dashboard.update.success', ToastTypeEnum.SUCCESS);
      this.refreshConnectedScreens();
    });
  }

  /**
   * Add a new grid to the current project
   *
   * @param formData The data retrieved from the side nav
   */
  private addNewGrid(formData: GridRequest): void {
    this.httpProjectGridsService.create(this.project.token, formData).subscribe((createdProjectGrid: ProjectGrid) => {
      this.router.navigate(['/dashboards', this.dashboardToken, createdProjectGrid.id]);
    });
  }

  /**
   * Execute the action edit the grids when the sidenav has been saved
   *
   * @param formData The data retrieve from the form sidenav
   */
  private editGrids(formData: ProjectGridRequest): void {
    const newTimes = Object.keys(formData)
      .filter(key => key.includes(ProjectFormFieldsService.timeFormFieldKey))
      .map(key => formData[key]);

    formData.grids = [];
    this.project.grids.forEach((grid, index) => {
      formData.grids.push({
        id: grid.id,
        time: newTimes[index]
      });
    });

    this.httpProjectGridsService.updateAll(this.project.token, formData).subscribe(() => {
      this.toastService.sendMessage('dashboard.update.success', ToastTypeEnum.SUCCESS);
      this.refreshConnectedScreens();
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

  private deleteDashboard(): void {
    this.dialogService.confirm({
      title: 'dashboard.delete',
      message: `${this.translateService.instant('dashboard.delete.confirm')} ${this.project.name.toUpperCase()} ?`,
      accept: () => {
        this.httpProjectService.delete(this.project.token).subscribe(() => {
          this.toastService.sendMessage('dashboard.delete.success', ToastTypeEnum.SUCCESS);
          this.router.navigate(['/home']);
        });
      }
    });
  }

  /**
   * Delete the current dashboard or grid
   */
  private deleteDashboardOrGrid(): void {
    this.dialogService.actions({
      title: 'dashboard.grid.delete',
      message: 'dashboard.grid.delete.dialog.message',
      actions: [
        {
          label: 'dashboard.grid.delete.dialog.select.grid',
          icon: IconEnum.GRID,
          color: 'warn',
          callback: () => {
            this.httpProjectGridsService.delete(this.project.token, this.gridId).subscribe(() => {
              this.toastService.sendMessage('dashboard.grid.delete.success', ToastTypeEnum.SUCCESS);
              this.router.navigate(['/dashboards', this.dashboardToken, this.project.grids[0].id]);
            });
          }
        },
        {
          label: 'dashboard.grid.delete.dialog.select.dashboard',
          icon: IconEnum.DASHBOARD,
          color: 'warn',
          callback: () => {
            this.httpProjectService.delete(this.project.token).subscribe(() => {
              this.toastService.sendMessage('dashboard.delete.success', ToastTypeEnum.SUCCESS);
              this.router.navigate(['/home']);
            });
          }
        }
      ]
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
