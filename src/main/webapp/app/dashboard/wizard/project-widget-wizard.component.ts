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

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/wizard/wizard.component.html',
  styleUrls: ['../../shared/components/wizard/wizard.component.scss']
})
export class ProjectWidgetWizardComponent extends WizardComponent implements OnInit {
  /**
   * Constructor
   *
   * @param injector Angular Service used to manage the injection of services
   * @param {ProjectWidgetFormStepsService} projectWidgetFormStepsService Frontend service used to build steps for project widget object
   */
  constructor(protected injector: Injector, private readonly projectWidgetFormStepsService: ProjectWidgetFormStepsService) {
    super(injector);

    this.initHeaderConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'Add widget'
    };
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
   * {@inheritDoc}
   */
  protected closeWizard(): void {
    const dashboardToken = RoutesService.getParamValueFromActivatedRoute(this.activatedRoute, 'dashboardToken');
    this.router.navigate(['/dashboards', dashboardToken]);
  }
}
