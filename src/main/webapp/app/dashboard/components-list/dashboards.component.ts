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

import { Component, Injector } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { Project } from '../../shared/models/backend/project/project';
import { HttpProjectService } from '../../shared/services/backend/http-project.service';
import { ProjectRequest } from '../../shared/models/backend/project/project-request';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { ProjectFormFieldsService } from '../../shared/form-fields/project-form-fields.service';
import { ProjectUsersFormFieldsService } from '../../shared/form-fields/project-users-form-fields.service';
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import { EMPTY, Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

/**
 * Component used to display the list of Dashboards
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class DashboardsComponent extends ListComponent<Project | ProjectRequest> {
  /**
   * Project selected in the list for modification
   * @type {Project}
   * @private
   */
  private projectSelected: Project;

  /**
   * Constructor
   *
   * @param {HttpProjectService} httpProjectService Suricate service used to manage the http calls for a project
   * @param {ProjectFormFieldsService} projectFormFieldsService Frontend service used to build form fields for a project
   * @param {ProjectUsersFormFieldsService} projectUsersFormFieldsService Frontend service used to build form fields for a project users
   * @param {Injector} injector Angular Service used to manage the injection of services
   */
  constructor(
    private readonly httpProjectService: HttpProjectService,
    private readonly projectFormFieldsService: ProjectFormFieldsService,
    private readonly projectUsersFormFieldsService: ProjectUsersFormFieldsService,
    protected injector: Injector
  ) {
    super(httpProjectService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboards' };
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
          callback: (event: Event, project: Project) => this.openUserFormSidenav(event, project)
        },
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, project: Project) => this.openFormSidenav(event, project, this.editProject.bind(this))
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          callback: (event: Event, project: Project) => this.deleteProject(event, project)
        }
      ]
    };
  }

  /**
   * {@inheritDoc}
   */
  protected getFirstLabel(project: Project): string {
    return project.name;
  }

  /**
   * {@inheritDoc}
   */
  protected getSecondLabel(project: Project): string {
    return project.token;
  }

  /**
   * {@inheritDoc}
   */
  protected redirectToBean(project: Project): void {
    this.router.navigate(['/dashboards', project.token]);
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param project The project clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, project: Project, saveCallback: (projectRequest: ProjectRequest) => void): void {
    this.stopEventPropagation(event);
    this.projectSelected = project;

    this.sidenavService.openFormSidenav({
      title: project ? 'dashboard.edit' : 'dashboard.add',
      formFields: ProjectFormFieldsService.generateProjectFormFields(project),
      save: (projectRequest: ProjectRequest) => saveCallback(projectRequest)
    });
  }

  /**
   * Redirect on the edit page
   *
   * @param projectRequest The project clicked on the list
   */
  private editProject(projectRequest: ProjectRequest): void {
    this.httpProjectService.update(this.projectSelected.token, projectRequest).subscribe(() => {
      this.refreshList();
    });
  }

  /**
   * Function used to delete a project
   *
   * @param event The click event
   * @param project The project to delete
   */
  private deleteProject(event: Event, project: Project): void {
    this.stopEventPropagation(event);

    this.dialogService.confirm({
      title: 'dashboard.delete',
      message: `${this.translateService.instant('delete.confirm')} ${project.name.toUpperCase()}`,
      accept: () => {
        this.httpProjectService.delete(project.token).subscribe(() => {
          this.toastService.sendMessage('Project deleted successfully', ToastTypeEnum.SUCCESS);
          this.refreshList();
        });
      }
    });
  }

  /**
   * Open the form sidenav used to manage users
   *
   * @param event The click event
   * @param project The project clicked on the list
   */
  private openUserFormSidenav(event: Event, project: Project): void {
    this.stopEventPropagation(event);
    this.projectSelected = project;

    this.sidenavService.openFormSidenav({
      title: 'user.add',
      formFields: this.projectUsersFormFieldsService.generateProjectUsersFormFields(project.token),
      hideSaveAction: true,
      onValueChanged: (valueChangedEvent: ValueChangedEvent) => this.onValueChanged(valueChangedEvent)
    });
  }

  /**
   * Function used to not propagate the event
   *
   * @param event The event to stop
   */
  private stopEventPropagation(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onValueChanged(valueChangedEvent: ValueChangedEvent): Observable<FormField[]> {
    if (valueChangedEvent.type === 'optionSelected' && valueChangedEvent.fieldKey === 'usernameAutocomplete') {
      return this.httpProjectService
        .addUserToProject(this.projectSelected.token, valueChangedEvent.value)
        .pipe(switchMap(() => of(this.projectUsersFormFieldsService.generateProjectUsersFormFields(this.projectSelected.token))));
    }

    return EMPTY;
  }
}
