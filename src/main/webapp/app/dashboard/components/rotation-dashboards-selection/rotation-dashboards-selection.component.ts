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
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { HttpRotationService } from '../../../shared/services/backend/http-rotation/http-rotation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { Project } from '../../../shared/models/backend/project/project';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { RotationFormFieldsService } from '../../../shared/services/frontend/form-fields/rotation-form-fields/rotation-form-fields.service';
import { Rotation } from '../../../shared/models/backend/rotation/rotation';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { RotationProjectRequest } from '../../../shared/models/backend/rotation-project/rotation-project-request';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { RotationProject } from '../../../shared/models/backend/rotation-project/rotation-project';

@Component({
  selector: 'suricate-rotation-creation',
  templateUrl: './rotation-dashboards-selection.component.html',
  styleUrls: ['./rotation-dashboards-selection.component.scss']
})
export class RotationDashboardsSelectionComponent implements OnInit {
  /**
   * Configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The list of nav buttons
   */
  public navButtons: ButtonConfiguration<unknown>[];

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The current rotation token, given by the route
   */
  public rotationToken: string;

  /**
   * The list of dashboards of the current user
   */
  public projects: Project[];

  /**
   * Dashboards to add to the rotation
   */
  public projectsToAddToRotation: RotationProjectRequest[] = [];

  /**
   * Used to know if the page is loading
   */
  public isLoading = true;

  /**
   * Constructor
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpProjectService: HttpProjectService,
    private readonly httpRotationService: HttpRotationService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService,
    private readonly rotationFormFieldsService: RotationFormFieldsService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.rotationToken = this.activatedRoute.snapshot.params['rotationToken'];
    this.initHeaderConfiguration();
    this.initNavButtons();

    this.httpRotationService.getProjectRotationsByRotationToken(this.rotationToken).subscribe((rotationProjects: RotationProject[]) => {
      if (rotationProjects) {
        const currentRotationProjectsOfRotation: RotationProjectRequest[] = rotationProjects.map(rotationProject => {
          return {
            projectToken: rotationProject.project.token,
            rotationSpeed: rotationProject.rotationSpeed
          };
        });

        this.projectsToAddToRotation.push(...currentRotationProjectsOfRotation);
      }

      this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
        this.isLoading = false;
        this.projects = dashboards;
      });
    });
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'rotation.add'
    };
  }

  /**
   * Init the buttons of the wizard
   */
  private initNavButtons(): void {
    this.navButtons = [
      {
        label: 'close',
        color: 'warn',
        callback: () => this.closeWizard()
      },
      {
        label: 'done',
        color: 'primary',
        callback: () => this.validateFormBeforeSave()
      }
    ];
  }

  /**
   * By default we redirect to home on close event (you can override this on child component)
   */
  protected closeWizard(): void {
    this.router.navigate(['/rotations/', this.activatedRoute.snapshot.params['rotationToken']]);
  }

  /**
   * Check if the stepper form is valid before saving the data
   */
  protected validateFormBeforeSave(): void {
    this.httpRotationService
      .addProjectsToRotation(this.activatedRoute.snapshot.params['rotationToken'], this.projectsToAddToRotation)
      .subscribe(() => {
        this.toastService.sendMessage('rotation.create.dashboard.added', ToastTypeEnum.SUCCESS);
        this.router.navigate(['/rotations/', this.activatedRoute.snapshot.params['rotationToken']]);
      });
  }

  /**
   * Open a sidenav for the options of the selected dashboard
   */
  public openDashboardOptionsSidenav(clickedProject: Project): void {
    const rotationProjectRequest = this.projectsToAddToRotation.find(projectToAdd => projectToAdd.projectToken === clickedProject.token);

    this.sidenavService.openFormSidenav({
      title: 'rotation.create.dashboard.options',
      formFields: this.rotationFormFieldsService.generateDashboardOptionsFormFields(clickedProject, rotationProjectRequest),
      save: (projectToAdd: RotationProjectRequest) => this.addNewDashboardToRotation(projectToAdd)
    });
  }

  /**
   * Add new dashboard to rotation
   *
   * @param rotationProjectRequest The rotation project request
   */
  private addNewDashboardToRotation(rotationProjectRequest: RotationProjectRequest): void {
    const isPresent = this.projectsToAddToRotation.findIndex(
      addedRotationProjectRequest => addedRotationProjectRequest.projectToken === rotationProjectRequest.projectToken
    );

    if (!rotationProjectRequest.rotationSpeed) {
      if (isPresent !== -1) {
        this.projectsToAddToRotation.splice(isPresent, 1);
      }
    } else {
      if (isPresent !== -1) {
        this.projectsToAddToRotation[isPresent] = rotationProjectRequest;
      } else {
        this.projectsToAddToRotation.push(rotationProjectRequest);
      }
    }
  }

  /**
   * Is the given project already selected for the rotation or not
   *
   * @param clickedProject The current project
   */
  public isProjectSelected(clickedProject: Project): boolean {
    return (
      this.projectsToAddToRotation.findIndex(rotationProjectRequest => rotationProjectRequest.projectToken === clickedProject.token) !== -1
    );
  }

  /**
   * Get the entered rotation speed of a project
   *
   * @param project The project to get the rotation
   */
  public getSelectedRotationSpeedByProject(project: Project): number {
    return this.projectsToAddToRotation.find(rotationProjectRequest => rotationProjectRequest.projectToken === project.token).rotationSpeed;
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
