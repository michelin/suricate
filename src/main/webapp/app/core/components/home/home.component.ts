/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { NgOptimizedImage } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { MatIcon } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { HeaderComponent } from '../../../layout/components/header/header.component';
import { SpinnerComponent } from '../../../shared/components/spinner/spinner.component';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { CssService } from '../../../shared/services/frontend/css/css.service';
import { ProjectFormFieldsService } from '../../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { FileUtils } from '../../../shared/utils/file.utils';
import { ImageUtils } from '../../../shared/utils/image.utils';

@Component({
  selector: 'suricate-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [
    HeaderComponent,
    SpinnerComponent,
    MatGridList,
    MatGridTile,
    MatIcon,
    RouterLink,
    NgOptimizedImage,
    TranslatePipe
  ]
})
export class HomeComponent implements OnInit {
  /**
   * Configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * Tell when the list of dashboards is loading
   */
  public isLoading: boolean;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of dashboards
   */
  public projects: Project[];

  /**
   * Constructor
   *
   * @param router The router
   * @param httpProjectService The HTTP project service
   * @param projectFormFieldsService The project form fields service
   * @param sidenavService The sidenav service
   * @param toastService The toast service
   */
  constructor(
    private readonly router: Router,
    private readonly httpProjectService: HttpProjectService,
    private readonly projectFormFieldsService: ProjectFormFieldsService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initHeaderConfiguration();

    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.isLoading = false;
      this.projects = dashboards;
    });
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'dashboard.list.my',
      actions: [
        {
          icon: IconEnum.ADD,
          variant: 'miniFab',
          type: ButtonTypeEnum.BUTTON,
          tooltip: { message: 'dashboard.create' },
          callback: () => this.openCreateDashboardFormSidenav()
        }
      ]
    };
  }

  /**
   * Display the side nav bar used to create a dashboard
   */
  public openCreateDashboardFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'dashboard.create',
      formFields: this.projectFormFieldsService.generateProjectFormFields(),
      save: (formGroup: UntypedFormGroup) => this.saveDashboard(formGroup)
    });
  }

  /**
   * Create a new dashboard
   *
   * @param formGroup The form group
   */
  private saveDashboard(formGroup: UntypedFormGroup): void {
    const formData: ProjectRequest = formGroup.value;
    formData.cssStyle = CssService.buildCssFile([CssService.buildCssGridBackgroundColor(formData.gridBackgroundColor)]);

    this.httpProjectService.create(formData).subscribe((project: Project) => {
      if (formData.image) {
        const contentType: string = ImageUtils.getContentTypeFromBase64URL(formData.image);
        const blob: Blob = FileUtils.base64ToBlob(
          ImageUtils.getDataFromBase64URL(formData.image),
          ImageUtils.getContentTypeFromBase64URL(formData.image)
        );

        const file: File = FileUtils.convertBlobToFile(blob, `${project.token}.${contentType.split('/')[1]}`);

        this.httpProjectService.addOrUpdateProjectScreenshot(project.token, file).subscribe();
      }

      this.toastService.sendMessage('dashboard.add.success', ToastTypeEnum.SUCCESS);
      this.router.navigate(['/dashboards', project.token, project.grids[0].id]);
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
