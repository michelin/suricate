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
import { DashboardTvManagementDialogComponent } from '../tv-management-dialog/dashboard-tv-management-dialog/dashboard-tv-management-dialog.component';
import { RotationTvManagementDialogComponent } from '../tv-management-dialog/rotation-tv-management-dialog/rotation-tv-management-dialog.component';
import { ProjectRotationUsersFormFieldsService } from '../../../shared/services/frontend/form-fields/project-rotation-users-form-fields/project-rotation-users-form-fields.service';
import { switchMap } from 'rxjs/operators';

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
   * The rotation to display
   */
  public rotation: Rotation;

  /**
   * Rotation object used during edition
   */
  public rotationInEdition: Rotation = new Rotation();

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
    this.httpRotationService.getByToken(this.activatedRoute.snapshot.params['rotationToken']).subscribe(
      (rotation: Rotation) => {
        this.isRotationLoading = false;
        this.rotation = rotation;
        this.initHeaderConfiguration();
      },
      () => {
        this.isRotationLoading = false;
        this.router.navigate(['/home/dashboards']);
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
          icon: IconEnum.TV_LIVE,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'screen.management' },
          hidden: () => !this.rotation || this.rotation.rotationProjects.length === 0,
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
   * Open the form sidenav used to edit the rotation
   */
  private openRotationFormSidenav(): void {
    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.projects = dashboards;

      // Make a copy of the rotation for the edition
      this.rotationInEdition = JSON.parse(JSON.stringify(this.rotation));

      this.sidenavService.openFormSidenav({
        title: 'rotation.edit',
        formFields: this.rotationFormFieldsService.generateRotationFormFields(dashboards, this.rotationInEdition),
        save: (formData: FormData) => this.editRotation(formData),
        onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onRotationSidenavValueChanged(valueChangedEvent)
      });
    });
  }

  /**
   * Save a new rotation
   *
   * @param formData The data retrieved from the form
   */
  private editRotation(formData: FormData): void {
    const rotationRequest: RotationRequest = {
      name: formData[RotationFormFieldsService.rotationNameFormFieldKey],
      rotationProjectRequests: []
    };

    formData[RotationFormFieldsService.projectsFormFieldKey].forEach(projectToken => {
      rotationRequest.rotationProjectRequests.push({
        projectToken: projectToken,
        rotationSpeed: formData[`${RotationFormFieldsService.rotationSpeedFormFieldKey}-${projectToken}`]
      });
    });

    this.httpRotationService.update(this.rotation.token, rotationRequest).subscribe(() => {
      this.toastService.sendMessage('rotation.update.success', ToastTypeEnum.SUCCESS);
      location.reload();
    });
  }

  /**
   * On rotation sidenav value changed, register the new value in a
   * rotation object and regenerate the form fields according to the
   * new values
   *
   * @param valueChangedEvent The value changed event
   */
  onRotationSidenavValueChanged(valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    if (valueChangedEvent.fieldKey === RotationFormFieldsService.projectsFormFieldKey) {
      // Add project
      valueChangedEvent.value.forEach(projectToken => {
        if (!this.rotationInEdition.rotationProjects.find(rotationProject => rotationProject.project.token === projectToken)) {
          const rotationProject: RotationProject = new RotationProject();
          rotationProject.project = this.projects.find(project => project.token === projectToken);
          this.rotationInEdition.rotationProjects.push(rotationProject);
        }
      });

      // Remove project
      this.rotationInEdition.rotationProjects.forEach(rotationProject => {
        if (!valueChangedEvent.value.includes(rotationProject.project.token)) {
          this.rotationInEdition.rotationProjects.splice(this.rotationInEdition.rotationProjects.indexOf(rotationProject), 1);
        }
      });

      return of(this.rotationFormFieldsService.generateRotationFormFields(this.projects, this.rotationInEdition));
    }

    if (valueChangedEvent.fieldKey.startsWith(RotationFormFieldsService.rotationSpeedFormFieldKey)) {
      this.rotationInEdition.rotationProjects.find(
        rotationProject => rotationProject.project.token === valueChangedEvent.fieldKey.substr(valueChangedEvent.fieldKey.indexOf('-') + 1)
      ).rotationSpeed = valueChangedEvent.value;
    } else {
      this.rotationInEdition[valueChangedEvent.fieldKey] = valueChangedEvent.value;
    }

    return EMPTY;
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
