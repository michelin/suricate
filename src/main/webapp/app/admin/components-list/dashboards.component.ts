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
import { TitleCasePipe } from '@angular/common';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';

/**
 * Component used to display the list of Dhasboards
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class DashboardsComponent extends ListComponent<Project | ProjectRequest> {
  /**
   * Constructor
   *
   * @param httpProjectService Suricate service used to manage the http calls for a project
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private httpProjectService: HttpProjectService, protected injector: Injector) {
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
          iconEnum: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, project: Project) => this.editRepository(event, project)
        },
        {
          iconEnum: IconEnum.DELETE,
          color: 'warn',
          callback: (event: Event, project: Project) => this.deleteRepository(event, project)
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
   * Redirect on the edit page
   *
   * @param event The click event
   * @param project The project clicked on the list
   */
  private editRepository(event: Event, project: Project): void {
    this.router.navigate(['/dashboards', 'all', project.token, 'edit']);
  }

  /**
   * Function used to delete a project
   *
   * @param event The click event
   * @param project The project to delete
   */
  private deleteRepository(event: Event, project: Project): void {
    this.translateService.get(['dashboard.delete', 'delete.confirm']).subscribe((translations: string[]) => {
      const titleCasePipe = new TitleCasePipe();

      this.dialogService.confirm({
        title: translations['dashboard.delete'],
        message: `${translations['delete.confirm']} ${titleCasePipe.transform(project.name)}`,
        accept: () => {
          this.httpProjectService.delete(project.token).subscribe(() => {
            this.refreshList();
            this.toastService.sendMessage('Project deleted successfully', ToastTypeEnum.SUCCESS);
          });
        }
      });
    });
  }
}
