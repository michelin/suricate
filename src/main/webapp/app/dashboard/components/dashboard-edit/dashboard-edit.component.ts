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
import { FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomValidators } from 'ng2-validation';
import { TranslateService } from '@ngx-translate/core';
import { flatMap, map } from 'rxjs/operators';

import { Project } from '../../../shared/models/backend/project/project';
import { ToastService } from '../../../shared/services/frontend/toast.service';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { User } from '../../../shared/models/backend/user/user';
import { FormStep } from '../../../shared/models/frontend/form/form-step';
import { FormService } from '../../../shared/services/frontend/form.service';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';

/**
 * Component that display the edit page for a dashboard
 */
@Component({
  selector: 'suricate-dashboard-edit',
  templateUrl: './dashboard-edit.component.html',
  styleUrls: ['./dashboard-edit.component.scss']
})
export class DashboardEditComponent implements OnInit {
  /**
   * The dashboard form
   * @type {FormGroup}
   */
  dashboardForm: FormGroup;

  /**
   * The list of step used to create the form
   * @type {FormStep[]}
   */
  formSteps: FormStep[];

  /**
   * The dashboard to edit
   * @type {Project}
   */
  dashboard: Project;

  /**
   * The list of project users
   */
  dashboardUsers: User[];

  /**
   * Constructor
   *
   * @param {HttpProjectService} httpProjectService The http project service to inject
   * @param {ActivatedRoute} activatedRoute The activated route to inject
   * @param {Router} router The router service
   * @param {ToastService} toastService The service used for displayed Toast notification
   * @param {FormService} formService The form service used to generate form
   * @param {TranslateService} translateService The service used to translate sentences
   */
  constructor(
    private httpProjectService: HttpProjectService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastService: ToastService,
    private formService: FormService,
    private translateService: TranslateService
  ) {}

  /**
   * Called when the component is displayed
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.httpProjectService
        .getById(params['dashboardToken'])
        .pipe(
          map((dashboard: Project) => (this.dashboard = dashboard)),
          flatMap(() => this.httpProjectService.getProjectUsers(params['dashboardToken'])),
          map((users: User[]) => (this.dashboardUsers = users))
        )
        .subscribe(() => {
          this.initDashboardForm();
        });
    });
  }

  /**
   * Init the dashboard edit form
   */
  initDashboardForm() {
    this.formSteps = [];
    this.generateStepOne()
      .pipe(map((stepOne: FormStep) => (this.formSteps[0] = stepOne)))
      .subscribe(() => {
        this.dashboardForm = this.formService.generateFormGroupForSteps(this.formSteps);
      });
  }

  /**
   * Generate the step one of the form
   * "General information step"
   */
  generateStepOne() {
    return this.translateService.get(['dashboard.name', 'token', 'widget.heigth.px', 'grid.nb.columns']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'name',
            label: translations['dashboard.name'],
            type: DataTypeEnum.TEXT,
            value: this.dashboard.name,
            matIconPrefix: 'loyalty',
            validators: [Validators.required, Validators.minLength(3)]
          },
          {
            key: 'token',
            label: translations['token'],
            type: DataTypeEnum.TEXT,
            value: this.dashboard.token,
            matIconPrefix: 'vpn_key',
            readOnly: true,
            validators: [Validators.required]
          },
          {
            key: 'widgetHeight',
            label: translations['widget.heigth.px'],
            type: DataTypeEnum.NUMBER,
            value: this.dashboard.gridProperties.widgetHeight,
            matIconPrefix: 'equalizer',
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          },
          {
            key: 'maxColumn',
            label: translations['grid.nb.columns'],
            type: DataTypeEnum.NUMBER,
            value: this.dashboard.gridProperties.maxColumn,
            matIconPrefix: 'view_week',
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          }
        ];

        return { fields: formFields };
      })
    );
  }

  /**
   * edit the dashboard
   */
  saveDashboard() {
    this.formService.validate(this.dashboardForm);

    if (this.dashboardForm.valid) {
      const projectRequest: ProjectRequest = { ...this.dashboard, ...this.dashboardForm.value };

      this.httpProjectService.update(this.dashboard.token, projectRequest).subscribe(() => {
        this.toastService.sendMessage('Dashboard saved successfully', ToastTypeEnum.SUCCESS);
        this.redirectToDashboardList();
      });
    }
  }

  /**
   * Redirect to dashboard list
   */
  redirectToDashboardList() {
    this.router.navigate(['/dashboards']);
  }
}
