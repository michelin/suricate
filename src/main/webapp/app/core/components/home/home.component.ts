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
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { ProjectFormFieldsService } from '../../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { CssService } from '../../../shared/services/frontend/css/css.service';
import { FileUtils } from '../../../shared/utils/file.utils';
import { ImageUtils } from '../../../shared/utils/image.utils';

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
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The list of dashboards
   */
  public dashboards: Project[];

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Tell when the list of dashboards is loading
   */
  public isLoading: boolean;

  /**
   * The constructor
   *
   * @param router Angular service used to manage routes
   * @param httpProjectService Suricate service used to manage http calls on projects
   * @param sidenavService Frontend service used to manage sidenav's
   * @param toastService Frontend service used to display toast messages
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
    this.isLoading = true;

    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.dashboards = dashboards;
      this.isLoading = false;
    });
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = { title: 'dashboard.list.my' };
  }

  /**
   * Function that display the sidenav used to create a new dashboard
   */
  public openDashboardFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'dashboard.add',
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

    projectRequest.cssStyle = CssService.buildCssFile([CssService.buildCssGridBackgroundColor(projectRequest['gridBackgroundColor'])]);

    this.httpProjectService.create(projectRequest).subscribe((project: Project) => {
      if (projectRequest.image) {
        const contentType: string = ImageUtils.getContentTypeFromBase64URL(projectRequest.image);
        const blob: Blob = FileUtils.base64ToBlob(
          ImageUtils.getDataFromBase64URL(projectRequest.image),
          ImageUtils.getContentTypeFromBase64URL(projectRequest.image)
        );
        const file: File = FileUtils.convertBlobToFile(blob, `${project.token}.${contentType.split('/')[1]}`, new Date());

        this.httpProjectService.addOrUpdateProjectScreenshot(project.token, file).subscribe();
      }

      this.toastService.sendMessage('dashboard.add.success', ToastTypeEnum.SUCCESS);
      this.router.navigate(['/dashboards', project.token]);
    });
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset used to build the url
   */
  public getContentUrl(assetToken: string): string {
    return HttpAssetService.getContentUrl(assetToken);
  }
}
