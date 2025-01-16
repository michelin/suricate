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

import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { WizardComponent } from '../../../../shared/components/wizard/wizard.component';
import { ToastTypeEnum } from '../../../../shared/enums/toast-type.enum';
import { Project } from '../../../../shared/models/backend/project/project';
import { ProjectWidget } from '../../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetRequest } from '../../../../shared/models/backend/project-widget/project-widget-request';
import { FormStep } from '../../../../shared/models/frontend/form/form-step';
import { HttpProjectService } from '../../../../shared/services/backend/http-project/http-project.service';
import { HttpProjectWidgetService } from '../../../../shared/services/backend/http-project-widget/http-project-widget.service';
import { ProjectWidgetFormStepsService } from '../../../../shared/services/frontend/form-steps/project-widget-form-steps/project-widget-form-steps.service';
import { ToastService } from '../../../../shared/services/frontend/toast/toast.service';
import { HeaderComponent } from '../../../../layout/components/header/header.component';
import { MatStepper, MatStepperIcon, MatStep, MatStepLabel } from '@angular/material/stepper';
import { MatIcon } from '@angular/material/icon';
import { NgOptimizedImage } from '@angular/common';
import { SlideToggleComponent } from '../../../../shared/components/inputs/slide-toggle/slide-toggle.component';
import { InputComponent } from '../../../../shared/components/inputs/input/input.component';
import { ButtonsComponent } from '../../../../shared/components/buttons/buttons.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: '../../../../shared/components/wizard/wizard.component.html',
    styleUrls: ['../../../../shared/components/wizard/wizard.component.scss'],
    standalone: true,
    imports: [HeaderComponent, MatStepper, MatStepperIcon, MatIcon, MatStep, MatStepLabel, NgOptimizedImage, SlideToggleComponent, FormsModule, ReactiveFormsModule, InputComponent, ButtonsComponent, TranslatePipe]
})
export class AddWidgetToProjectWizardComponent extends WizardComponent implements OnInit {
  /**
   * Constructor
   * @param projectWidgetFormStepsService The project widget form steps service
   * @param httpProjectWidgetsService The http project widget service
   * @param httpProjectService The http project service
   * @param toastService The toast service
   */
  constructor(
    private readonly projectWidgetFormStepsService: ProjectWidgetFormStepsService,
    private readonly httpProjectWidgetsService: HttpProjectWidgetService,
    private readonly httpProjectService: HttpProjectService,
    private readonly toastService: ToastService
  ) {
    super();
    this.initHeaderConfiguration();
  }

  /**
   * Called when the component is init
   */
  public override ngOnInit(): void {
    this.projectWidgetFormStepsService.generateGlobalSteps().subscribe((formSteps: FormStep[]) => {
      this.wizardConfiguration = { steps: formSteps };

      super.ngOnInit();
    });
  }

  /**
   * Function used to configure the header of this wizard component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'widget.add'
    };
  }

  /**
   * {@inheritDoc}
   */
  protected override closeWizard(): void {
    this.redirectToDashboard();
  }

  /**
   * {@inheritDoc}
   */
  protected override saveWizard(formGroup: UntypedFormGroup): void {
    this.httpProjectService.getById(this.dashboardToken).subscribe((project: Project) => {
      this.httpProjectWidgetsService.getAllByProjectToken(this.dashboardToken).subscribe((widgets: ProjectWidget[]) => {
        let row = 1;
        let column = 1;
        if (widgets != null && widgets.length > 0) {
          const widgetsByGrid = widgets.filter((widget) => widget.gridId === this.gridId);
          while (
            widgetsByGrid.filter(
              (widget) => widget.widgetPosition.gridRow === row && widget.widgetPosition.gridColumn === column
            ).length > 0
          ) {
            column += widgetsByGrid.filter(
              (widget) => widget.widgetPosition.gridRow === row && widget.widgetPosition.gridColumn === column
            )[0].widgetPosition.width;
            if (column > project.gridProperties.maxColumn) {
              column = 1;
              row++;
            }
          }
        }

        const projectWidgetRequest: ProjectWidgetRequest = {
          widgetId: formGroup.get(ProjectWidgetFormStepsService.selectWidgetStepKey).value[
            ProjectWidgetFormStepsService.widgetIdFieldKey
          ],
          backendConfig: Object.keys(formGroup.get(ProjectWidgetFormStepsService.configureWidgetStepKey).value)
            .filter(
              (key: string) =>
                formGroup.get(ProjectWidgetFormStepsService.configureWidgetStepKey).value[key] != null &&
                String(formGroup.get(ProjectWidgetFormStepsService.configureWidgetStepKey).value[key]).trim() !== ''
            )
            .map(
              (key: string) =>
                `${key}=${String(formGroup.get(ProjectWidgetFormStepsService.configureWidgetStepKey).value[key]).replace(/\n/g, '\\n')}`
            )
            .join('\n'),
          gridColumn: column,
          gridRow: row
        };

        this.httpProjectWidgetsService
          .addProjectWidgetToProject(this.dashboardToken, this.gridId, projectWidgetRequest)
          .subscribe(() => {
            this.toastService.sendMessage('widget.add.success', ToastTypeEnum.SUCCESS);
            this.redirectToDashboard();
          });
      });
    });
  }

  /**
   * Function used to redirect to the dashboard
   */
  private redirectToDashboard(): void {
    this.router.navigate(['/dashboards', this.dashboardToken, this.gridId]);
  }
}
