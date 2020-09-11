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

import { Component, Injector, OnInit } from '@angular/core';
import { WizardComponent } from '../../shared/components/wizard/wizard.component';
import { ProjectWidgetFormStepsService } from '../../shared/form-steps/project-widget-form-steps.service';
import { FormStep } from '../../shared/models/frontend/form/form-step';
import { RoutesService } from '../../shared/services/frontend/route.service';
import { ProjectWidgetRequest } from '../../shared/models/backend/project-widget/project-widget-request';
import { HttpProjectService } from '../../shared/services/backend/http-project.service';
import { ToastService } from '../../shared/services/frontend/toast.service';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/wizard/wizard.component.html',
  styleUrls: ['../../shared/components/wizard/wizard.component.scss']
})
export class ProjectWidgetWizardComponent extends WizardComponent implements OnInit {
  /**
   * The token of the dashboard
   */
  private readonly dashboardToken: string;

  /**
   * Constructor
   *
   * @param {Injector} injector Angular Service used to manage the injection of services
   * @param {HttpProjectService} httpProjectService Suricate service used to manage http calls for a dashboard
   * @param {ProjectWidgetFormStepsService} projectWidgetFormStepsService Frontend service used to build steps for project widget object
   * @param {ToastService} toastService Frontend service used to display message
   */
  constructor(
    protected injector: Injector,
    private readonly httpProjectService: HttpProjectService,
    private readonly projectWidgetFormStepsService: ProjectWidgetFormStepsService,
    private readonly toastService: ToastService
  ) {
    super(injector);
    this.dashboardToken = RoutesService.getParamValueFromActivatedRoute(this.activatedRoute, 'dashboardToken');
    this.initHeaderConfiguration();
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.projectWidgetFormStepsService.generateGlobalSteps().subscribe((formSteps: FormStep[]) => {
      this.wizardConfiguration = { steps: formSteps };

      super.ngOnInit();
    });
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'widget.add'
    };
  }

  /**
   * {@inheritDoc}
   */
  protected closeWizard(): void {
    this.redirectToDashboard();
  }

  /**
   * {@inheritDoc}
   */
  protected saveWizard(formData: FormData): void {
    const projectWidgetRequest: ProjectWidgetRequest = {
      widgetId: formData[ProjectWidgetFormStepsService.selectWidgetStepKey][ProjectWidgetFormStepsService.widgetIdFieldKey],
      backendConfig: Object.keys(formData[ProjectWidgetFormStepsService.configureWidgetStepKey])
        .filter((key: string) => formData[ProjectWidgetFormStepsService.configureWidgetStepKey][key])
        .map((key: string) => `${key}=${formData[ProjectWidgetFormStepsService.configureWidgetStepKey][key]}`)
        .join('\n')
    };

    this.httpProjectService.addProjectWidgetToProject(this.dashboardToken, projectWidgetRequest).subscribe(() => {
      this.toastService.sendMessage('widget.add.success', ToastTypeEnum.SUCCESS);
      this.redirectToDashboard();
    });
  }

  /**
   * Function used to redirect to the dashbaord
   */
  private redirectToDashboard(): void {
    this.router.navigate(['/dashboards', this.dashboardToken]);
  }
}
