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

import { Component, Injector } from '@angular/core';
import { HttpRotationService } from '../../shared/services/backend/http-rotation/http-rotation.service';
import { ListComponent } from '../../shared/components/list/list.component';
import { Project } from '../../shared/models/backend/project/project';
import { RotationRequest } from '../../shared/models/backend/rotation/rotation-request';
import { Rotation } from '../../shared/models/backend/rotation/rotation';
import { IconEnum } from '../../shared/enums/icon.enum';
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import { ProjectRotationUsersFormFieldsService } from '../../shared/services/frontend/form-fields/project-rotation-users-form-fields/project-rotation-users-form-fields.service';
import { EMPTY, Observable, of } from 'rxjs';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { switchMap } from 'rxjs/operators';
import { RotationFormFieldsService } from '../../shared/services/frontend/form-fields/rotation-form-fields/rotation-form-fields.service';
import { HttpProjectService } from '../../shared/services/backend/http-project/http-project.service';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { RotationProject } from '../../shared/models/backend/rotation-project/rotation-project';

@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class RotationsComponent extends ListComponent<Rotation | RotationRequest> {
  /**
   * The list of dashboards
   */
  public projects: Project[];

  /**
   * Rotation selected in the list for modifications
   */
  private rotationSelected: Rotation;

  /**
   * Constructor
   *
   * @param httpRotationService Manage the http calls for a rotation
   * @param httpProjectService Manage the http calls for a project
   * @param rotationFormFieldsService Build form fields for a rotation
   * @param rotationUsersFormFieldsService Build form fields for rotation users
   * @param injector Manage the injection of services
   */
  constructor(
    private readonly httpRotationService: HttpRotationService,
    private readonly httpProjectService: HttpProjectService,
    private readonly rotationFormFieldsService: RotationFormFieldsService,
    private readonly rotationUsersFormFieldsService: ProjectRotationUsersFormFieldsService,
    protected injector: Injector
  ) {
    super(httpRotationService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
    this.initFilter();
  }

  /**
   * Function used to not propagate the event
   *
   * @param event The event to stop
   */
  private static stopEventPropagation(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'rotation.list' };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      enableShowBean: true,
      buttons: [
        {
          icon: IconEnum.USERS,
          color: 'primary',
          callback: (event: Event, rotation: Rotation) => this.openUserFormSidenav(event, rotation)
        },
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, rotation: Rotation) => this.openFormSidenav(event, rotation)
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          callback: (event: Event, rotation: Rotation) => this.deleteProject(event, rotation)
        }
      ]
    };
  }

  /**
   * {@inheritDoc}
   */
  public getFirstLabel(rotation: Rotation): string {
    return rotation.name;
  }

  /**
   * {@inheritDoc}
   */
  public getSecondLabel(rotation: Rotation): string {
    return rotation.token;
  }

  /**
   * {@inheritDoc}
   */
  public redirectToBean(rotation: Rotation): void {
    this.router.navigate(['/rotations', rotation.token]);
  }

  /**
   * Open the form sidenav used to manage users
   *
   * @param event The click event
   * @param rotation The rotation clicked on the list
   */
  private openUserFormSidenav(event: Event, rotation: Rotation): void {
    RotationsComponent.stopEventPropagation(event);
    this.rotationSelected = rotation;

    this.sidenavService.openFormSidenav({
      title: 'user.add',
      formFields: this.rotationUsersFormFieldsService.generateRotationUsersFormFields(rotation.token),
      hideSaveAction: true,
      onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onValueChanged(valueChangedEvent)
    });
  }

  /**
   * On value changed callback, called when selecting a user to add to a dashboard
   *
   * @param valueChangedEvent The event
   */
  private onValueChanged(valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    if (valueChangedEvent.type === 'optionSelected' && valueChangedEvent.fieldKey === 'usernameAutocomplete') {
      return this.httpRotationService
        .addUserToRotation(this.rotationSelected.token, valueChangedEvent.value)
        .pipe(switchMap(() => of(this.rotationUsersFormFieldsService.generateRotationUsersFormFields(this.rotationSelected.token))));
    }

    return EMPTY;
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param rotation The project clicked on the list
   */
  private openFormSidenav(event: Event, rotation: Rotation): void {
    RotationsComponent.stopEventPropagation(event);
    this.rotationSelected = rotation;

    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.projects = dashboards;

      this.sidenavService.openFormSidenav({
        title: rotation ? 'rotation.edit' : 'rotation.create',
        formFields: this.rotationFormFieldsService.generateRotationFormFields(dashboards, rotation),
        save: (formData: FormData) => this.editRotation(formData),
        onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onRotationSidenavValueChanged(rotation, valueChangedEvent)
      });
    });
  }

  /**
   * On rotation sidenav value changed, register the new value in a
   * rotation object and regenerate the form fields according to the
   * new values
   *
   * @param valueChangedEvent The value changed event
   */
  public onRotationSidenavValueChanged(rotation: Rotation, valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    if (valueChangedEvent.fieldKey === RotationFormFieldsService.projectsFormFieldKey) {
      // Add project
      valueChangedEvent.value.forEach(projectToken => {
        if (!rotation.rotationProjects.find(rotationProject => rotationProject.project.token === projectToken)) {
          const rotationProject: RotationProject = new RotationProject();
          rotationProject.project = this.projects.find(project => project.token === projectToken);
          rotation.rotationProjects.push(rotationProject);
        }
      });

      // Remove project
      rotation.rotationProjects.forEach(rotationProject => {
        if (!valueChangedEvent.value.includes(rotationProject.project.token)) {
          rotation.rotationProjects.splice(rotation.rotationProjects.indexOf(rotationProject), 1);
        }
      });

      return of(this.rotationFormFieldsService.generateRotationFormFields(this.projects, rotation));
    }

    if (valueChangedEvent.fieldKey.startsWith(RotationFormFieldsService.rotationSpeedFormFieldKey)) {
      rotation.rotationProjects.find(
        rotationProject => rotationProject.project.token === valueChangedEvent.fieldKey.substr(valueChangedEvent.fieldKey.indexOf('-') + 1)
      ).rotationSpeed = valueChangedEvent.value;
    } else {
      rotation[valueChangedEvent.fieldKey] = valueChangedEvent.value;
    }

    return EMPTY;
  }

  /**
   * Update the rotation
   *
   * @param formData
   * @private
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

    this.httpRotationService.update(this.rotationSelected.token, rotationRequest).subscribe(() => {
      this.toastService.sendMessage('rotation.update.success', ToastTypeEnum.SUCCESS);
      this.refreshList();
    });
  }

  /**
   * Function used to delete a rotation
   *
   * @param event The click event
   * @param rotation The rotation to delete
   */
  private deleteProject(event: Event, rotation: Rotation): void {
    RotationsComponent.stopEventPropagation(event);

    this.dialogService.confirm({
      title: 'rotation.delete',
      message: `${this.translateService.instant('delete.confirm')} ${rotation.name.toUpperCase()} ?`,
      accept: () => {
        this.httpRotationService.delete(rotation.token).subscribe(() => {
          this.toastService.sendMessage('rotation.delete.success', ToastTypeEnum.SUCCESS);
          this.refreshList();
        });
      }
    });
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['name,asc'];
  }
}
