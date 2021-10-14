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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpRotationService } from '../../../shared/services/backend/http-rotation/http-rotation.service';
import { Rotation } from '../../../shared/models/backend/rotation/rotation';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { DialogService } from '../../../shared/services/frontend/dialog/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { RotationFormFieldsService } from '../../../shared/services/frontend/form-fields/rotation-form-fields/rotation-form-fields.service';
import { Project } from '../../../shared/models/backend/project/project';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { ValueChangedEvent } from '../../../shared/models/frontend/form/value-changed-event';
import { RotationRequest } from '../../../shared/models/backend/rotation/rotation-request';
import { EMPTY, Observable, of } from 'rxjs';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { RotationProject } from '../../../shared/models/backend/rotation-project/rotation-project';
import { MatDialog } from '@angular/material/dialog';
import { RotationTvManagementDialogComponent } from '../tv-management-dialog/rotation-tv-management-dialog/rotation-tv-management-dialog.component';
import { ProjectRotationUsersFormFieldsService } from '../../../shared/services/frontend/form-fields/project-rotation-users-form-fields/project-rotation-users-form-fields.service';
import { flatMap, map, switchMap, tap } from 'rxjs/operators';
import { HttpScreenService } from '../../../shared/services/backend/http-screen/http-screen.service';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';

@Component({
  selector: 'suricate-rotation-detail',
  templateUrl: './rotation-detail.component.html',
  styleUrls: ['./rotation-detail.component.scss']
})
export class RotationDetailComponent implements OnInit {
  /**
   * The list of dashboards
   */
  public projects: Project[];

  /**
   * Hold the configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The token of the rotation
   */
  public rotationToken: string;

  /**
   * The rotation to display
   */
  public rotation: Rotation;

  /**
   * The list of project rotations
   */
  public rotationProjects: RotationProject[];

  /**
   * Used to know if the rotation is loading
   */
  public isRotationLoading = true;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * Constructor
   *
   * @param router The router
   * @param matDialog The mat dialog
   * @param activatedRoute The activated route
   * @param httpRotationService The HTTP rotation service
   * @param httpProjectService The HTTP project service
   * @param httpScreenService The HTTP screen service
   * @param dialogService The dialog service
   * @param translateService The translate service
   * @param toastService The toast service
   * @param sidenavService The sidenav service
   * @param rotationFormFieldsService The rotation form fields service
   * @param rotationUsersFormFieldsService The rotation users form field service
   */
  constructor(
    private readonly router: Router,
    private readonly matDialog: MatDialog,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpRotationService: HttpRotationService,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpScreenService: HttpScreenService,
    private readonly dialogService: DialogService,
    private readonly translateService: TranslateService,
    private readonly toastService: ToastService,
    private readonly sidenavService: SidenavService,
    private readonly rotationFormFieldsService: RotationFormFieldsService,
    private readonly rotationUsersFormFieldsService: ProjectRotationUsersFormFieldsService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.rotationToken = this.activatedRoute.snapshot.params['rotationToken'];

    this.refreshRotation()
      .pipe(flatMap(() => this.refreshProjectRotations()))
      .subscribe(
        () => {
          this.isRotationLoading = false;
          this.initHeaderConfiguration();
        },
        () => {
          this.isRotationLoading = false;
          this.router.navigate(['/home/rotations']);
        }
      );
  }

  /**
   * Init the header of the rotation detail screen
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: this.rotation.name,
      actions: [
        {
          icon: IconEnum.DASHBOARD_ROTATION,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'rotation.update.dashboards' },
          hidden: () => !this.rotationProjects,
          callback: () => this.displayRotationCreation()
        },
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'rotation.edit' },
          callback: () => this.openRotationFormSidenav()
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
          callback: () => this.refreshConnectedScreens()
        },
        {
          icon: IconEnum.TV,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'tv.view' },
          callback: () => this.redirectToTvView()
        },
        {
          icon: IconEnum.TV_LIVE,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.management' },
          hidden: () => !this.rotation || !this.rotationProjects,
          callback: () => this.openScreenManagementDialog()
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          variant: 'miniFab',
          tooltip: { message: 'rotation.delete' },
          callback: () => this.deleteDashboard()
        }
      ]
    };
  }

  /**
   * Refresh the rotation
   */
  private refreshRotation(): Observable<Rotation> {
    return this.httpRotationService.getById(this.rotationToken).pipe(tap((rotation: Rotation) => (this.rotation = rotation)));
  }

  /**
   * Refresh the list of project rotations
   */
  private refreshProjectRotations(): Observable<RotationProject[]> {
    return this.httpRotationService
      .getProjectRotationsByRotationToken(this.rotationToken)
      .pipe(tap((rotationProjects: RotationProject[]) => (this.rotationProjects = rotationProjects)));
  }

  /**
   * Redirect to the wizard used to add a new project to the rotation
   */
  public displayRotationCreation(): void {
    this.router.navigate(['/rotations', this.rotation.token, 'select']);
  }

  /**
   * Open the form sidenav used to edit the rotation
   */
  private openRotationFormSidenav(): void {
    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.projects = dashboards;

      this.sidenavService.openFormSidenav({
        title: 'rotation.edit',
        formFields: this.rotationFormFieldsService.generateRotationFormFields(this.rotation),
        save: (rotationRequest: RotationRequest) => this.editRotation(rotationRequest)
      });
    });
  }

  /**
   * Edit a rotation
   *
   * @param rotationRequest The data retrieved from the form
   */
  private editRotation(rotationRequest: RotationRequest): void {
    this.httpRotationService.update(this.rotation.token, rotationRequest).subscribe(() => {
      this.toastService.sendMessage('rotation.update.success', ToastTypeEnum.SUCCESS);
      location.reload();
    });
  }

  /**
   * Delete the current rotation
   */
  private deleteDashboard(): void {
    this.dialogService.confirm({
      title: 'dashboard.delete',
      message: `${this.translateService.instant('delete.confirm')} ${this.rotation.name.toUpperCase()} ?`,
      accept: () => {
        this.httpRotationService.delete(this.rotation.token).subscribe(() => {
          this.toastService.sendMessage('rotation.delete.success', ToastTypeEnum.SUCCESS);
          this.router.navigate(['/home/rotations']);
        });
      }
    });
  }

  /**
   * Open the user form sidenav
   */
  private openUserFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'user.add',
      formFields: this.rotationUsersFormFieldsService.generateRotationUsersFormFields(this.rotation.token),
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
      return this.httpRotationService
        .addUserToRotation(this.rotation.token, valueChangedEvent.value)
        .pipe(switchMap(() => of(this.rotationUsersFormFieldsService.generateRotationUsersFormFields(this.rotation.token))));
    }

    return EMPTY;
  }

  /**
   * Refresh every connected screens that render the current rotation
   */
  private refreshConnectedScreens(): void {
    this.httpScreenService.refreshEveryConnectedScreensForRotation(this.rotation.token).subscribe();
  }

  /**
   * Open a new tab with the TV view
   */
  private redirectToTvView(): void {
    const url = this.router.createUrlTree(['/tv'], { queryParams: { rotation: this.rotation.token } });
    window.open(url.toString(), '_blank');
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset used to build the url
   */
  public getContentUrl(assetToken: string): string {
    return HttpAssetService.getContentUrl(assetToken);
  }

  /**
   * Open the dialog used to manage screens
   */
  private openScreenManagementDialog(): void {
    this.matDialog.open(RotationTvManagementDialogComponent, {
      role: 'dialog',
      width: '700px',
      maxHeight: '80%',
      autoFocus: false,
      data: { rotation: this.rotation }
    });
  }
}
