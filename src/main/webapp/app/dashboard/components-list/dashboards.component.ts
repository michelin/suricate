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
import { User } from '../../shared/models/backend/user/user';
import { ProjectUsersFormFieldsService } from '../../shared/form-fields/project-users-form-fields.service';

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
   */
  private projectSelected: Project;

  private filter: string;

  /**
   * Constructor
   *
   * @param httpProjectService Suricate service used to manage the http calls for a project
   * @param projectFormFieldsService Frontend service used to build form fields for a project
   * @param projectUsersFormFieldsService Frontend service used to build form fields for a project users
   * @param injector Angular Service used to manage the injection of services
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
    this.headerConfiguration = {
      title: 'dashboards'
    };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.USERS,
          color: 'primary',
          callback: (event: Event, project: Project) => this.openUserFormSidenav(event, project, this.saveUsers.bind(this))
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
   * Open the form sidenav
   *
   * @param event The click event
   * @param project The project clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(event: Event, project: Project, saveCallback: (projectRequest: ProjectRequest) => void): void {
    this.projectSelected = project;

    this.translateService.get(['dashboard.edit', 'dashboard.add']).subscribe((translations: string[]) => {
      this.projectFormFieldsService.generateProjectFormFields(project).subscribe((formFields: FormField[]) => {
        this.sidenavService.openFormSidenav({
          title: project ? translations['dashboard.edit'] : translations['dashboard.add'],
          formFields: formFields,
          save: (projectRequest: ProjectRequest) => saveCallback(projectRequest)
        });
      });
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
    this.translateService.get(['dashboard.delete', 'delete.confirm']).subscribe((translations: string[]) => {
      this.dialogService.confirm({
        title: translations['dashboard.delete'],
        message: `${translations['delete.confirm']} ${project.name.toUpperCase()}`,
        accept: () => {
          this.httpProjectService.delete(project.token).subscribe(() => {
            this.toastService.sendMessage('Project deleted successfully', ToastTypeEnum.SUCCESS);
            this.refreshList();
          });
        }
      });
    });
  }

  /**
   * Open the form sidenav used to manage users
   * @param event The click event
   * @param project The project clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openUserFormSidenav(event: Event, project: Project, saveCallback: (users: User[]) => void): void {
    this.projectSelected = project;

    this.translateService.get(['user.add']).subscribe((translations: string[]) => {
      this.projectUsersFormFieldsService.generateProjectUsersFormFields(project.token).subscribe((formFields: FormField[]) => {
        this.sidenavService.openFormSidenav({
          title: translations['user.add'],
          formFields: formFields,
          save: (users: User[]) => saveCallback(users)
        });
      });
    });
  }

  /**
   * Save the users related to a project
   *
   * @param event The click event
   * @param users The users to add to the project
   */
  private saveUsers(event: Event, users: User[]): void {}
}
