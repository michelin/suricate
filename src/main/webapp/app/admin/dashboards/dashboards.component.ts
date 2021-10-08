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
import { ListComponent } from '../../shared/components/list/list.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { Project } from '../../shared/models/backend/project/project';
import { HttpProjectService } from '../../shared/services/backend/http-project/http-project.service';
import { ProjectRequest } from '../../shared/models/backend/project/project-request';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { FormField } from '../../shared/models/frontend/form/form-field';
import { ProjectFormFieldsService } from '../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import { ProjectRotationUsersFormFieldsService } from '../../shared/services/frontend/form-fields/project-rotation-users-form-fields/project-rotation-users-form-fields.service';
import { ValueChangedEvent } from '../../shared/models/frontend/form/value-changed-event';
import { EMPTY, Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CssService } from '../../shared/services/frontend/css/css.service';

@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class DashboardsComponent extends ListComponent<Project | ProjectRequest> {
  /**
   * Project selected in the list for modifications
   */
  private projectSelected: Project;

  /**
   * Constructor
   *
   * @param httpProjectService Manage the http calls for a project
   * @param projectFormFieldsService Build form fields for a project
   * @param projectUsersFormFieldsService Build form fields for rotation users
   * @param injector Manage the injection of services
   */
  constructor(
    private readonly httpProjectService: HttpProjectService,
    private readonly projectFormFieldsService: ProjectFormFieldsService,
    private readonly projectUsersFormFieldsService: ProjectRotationUsersFormFieldsService,
    protected injector: Injector
  ) {
    super(httpProjectService, injector);

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
  public redirectToBean(project: Project): void {
    this.router.navigate(['/dashboards', project.token]);
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboard.list' };
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
          callback: (event: Event, project: Project) => this.openFormSidenav(event, project)
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
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['name,asc'];
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param project The project clicked on the list
   */
  private openFormSidenav(event: Event, project: Project): void {
    DashboardsComponent.stopEventPropagation(event);
    this.projectSelected = project;

    this.sidenavService.openFormSidenav({
      title: project ? 'dashboard.edit' : 'dashboard.create',
      formFields: this.projectFormFieldsService.generateProjectFormFields(project),
      save: (projectRequest: ProjectRequest) => this.editProject(projectRequest)
    });
  }

  /**
   * Redirect on the edit page
   *
   * @param projectRequest The project clicked on the list
   */
  private editProject(projectRequest: ProjectRequest): void {
    projectRequest.cssStyle = CssService.buildCssFile([CssService.buildCssGridBackgroundColor(projectRequest.gridBackgroundColor)]);

    this.httpProjectService.update(this.projectSelected.token, projectRequest).subscribe(() => {
      this.toastService.sendMessage('dashboard.update.success', ToastTypeEnum.SUCCESS);
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
    DashboardsComponent.stopEventPropagation(event);

    this.dialogService.confirm({
      title: 'dashboard.delete',
      message: `${this.translateService.instant('delete.confirm')} ${project.name.toUpperCase()} ?`,
      accept: () => {
        this.httpProjectService.delete(project.token).subscribe(() => {
          this.toastService.sendMessage('dashboard.delete.success', ToastTypeEnum.SUCCESS);
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
    DashboardsComponent.stopEventPropagation(event);
    this.projectSelected = project;

    this.sidenavService.openFormSidenav({
      title: 'user.add',
      formFields: this.projectUsersFormFieldsService.generateProjectUsersFormFields(project.token),
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
      return this.httpProjectService
        .addUserToProject(this.projectSelected.token, valueChangedEvent.value)
        .pipe(switchMap(() => of(this.projectUsersFormFieldsService.generateProjectUsersFormFields(this.projectSelected.token))));
    }

    return EMPTY;
  }
}
