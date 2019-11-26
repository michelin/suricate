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

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Project } from '../../../shared/models/backend/project/project';
import { HttpAssetService } from '../../../shared/services/backend/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { SidenavService } from '../../../shared/services/frontend/sidenav.service';
import { ProjectFormFieldsService } from '../../../shared/form-fields/project-form-fields.service';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { ToastService } from '../../../shared/services/frontend/toast.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';

/**
 * Manage the home page
 */
@Component({
  selector: 'suricate-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  /**
   * Configuration of the header
   * @type {HeaderConfiguration}
   * @protected
   */
  protected headerConfiguration: HeaderConfiguration;
  /**
   * The list of dashboards
   * @type {Project[]}
   * @protected
   */
  protected dashboards: Project[];

  /**
   * The constructor
   *
   * @param {Router} router Angular service used to manage routes
   * @param {HttpProjectService} httpProjectService Suricate service used to manage http calls on projects
   * @param {SidenavService} sidenavService Frontend service used to manage sidenav's
   * @param {ToastService} toastService Frontend service used to display toast messages
   */
  constructor(
    private readonly router: Router,
    private readonly httpProjectService: HttpProjectService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService
  ) {
    this.initHeaderConfiguration();
  }

  /**
   * Called when the home page is init
   */
  public ngOnInit(): void {
    this.httpProjectService.getAllForCurrentUser().subscribe(dashboards => {
      this.dashboards = dashboards;
    });
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboards.my' };
  }

  /**
   * Function that display the sidenav used to create a new dashboard
   */
  protected openDashboardFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'Create dashboard',
      formFields: ProjectFormFieldsService.generateProjectFormFields(),
      save: (formData: ProjectRequest) => this.addDashboard(formData)
    });
  }

  /**
   * Called by the form sidenav used to create a new dashboard on save
   *
   * @param projectRequest The request to send to the backend with the information written on the form
   */
  private addDashboard(projectRequest: ProjectRequest): void {
    projectRequest.cssStyle = `.grid { background-color: ${projectRequest['gridBackgroundColor']}; }`;

    this.httpProjectService.create(projectRequest).subscribe((project: Project) => {
      this.toastService.sendMessage('Project created successfully', ToastTypeEnum.SUCCESS);
      this.router.navigate(['/dashboards', project.token]);
    });
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset used to build the url
   */
  protected getContentUrl(assetToken: string): string {
    return HttpAssetService.getContentUrl(assetToken);
  }
}
