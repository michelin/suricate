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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

import { Project } from '../../../shared/models/backend/project/project';
import { DashboardService } from '../../../dashboard/services/dashboard.service';
import { HttpAssetService } from '../../../shared/services/backend/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { SidenavService } from '../../../shared/services/frontend/sidenav.service';
import { ProjectFormFieldsService } from '../../../shared/form-fields/project-form-fields.service';
import { FormField } from '../../../shared/models/frontend/form/form-field';
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
export class HomeComponent implements OnInit, OnDestroy {
  protected headerConfiguration: HeaderConfiguration;
  /**
   * True while the component is instantiate
   * @type {boolean}
   */
  private isAlive = true;

  /**
   * The list of dashboards
   * @type {Project[]}
   */
  dashboards: Project[];

  /**
   * The constructor
   *
   * @param {DashboardService} dashboardService The dashboard service
   * @param {HttpAssetService} httpAssetService The http asset service to inject
   * @param {MatDialog} matDialog The mat dialog service
   * @param {Router} router The router service
   */
  constructor(
    private dashboardService: DashboardService,
    private readonly httpProjectService: HttpProjectService,
    private httpAssetService: HttpAssetService,
    private matDialog: MatDialog,
    private readonly sidenavService: SidenavService,
    private readonly projectFormFieldsService: ProjectFormFieldsService,
    private router: Router,
    private readonly toastService: ToastService
  ) {
    this.initHeaderConfiguration();
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.httpProjectService.getAllForCurrentUser().subscribe(dashboards => {
      this.dashboards = dashboards;
    });
  }

  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboards.my' };
  }

  protected openDashboardFormSidenav(): void {
    this.projectFormFieldsService.generateProjectFormFields().subscribe((formFields: FormField[]) => {
      this.sidenavService.openFormSidenav({
        title: 'Create dashboard',
        formFields: formFields,
        save: (formData: ProjectRequest) => this.addDashboard(formData)
      });
    });
  }

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
   * @param assetToken The asset token
   */
  getContentUrl(assetToken: string): string {
    return this.httpAssetService.getContentUrl(assetToken);
  }

  /**
   * Called when the component is destroy
   */
  ngOnDestroy() {
    this.isAlive = false;
  }
}
