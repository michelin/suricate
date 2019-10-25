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

import { Project } from '../../../../../shared/model/api/project/Project';
import { ToastService } from '../../../../../shared/components/toast/toast.service';
import { ToastType } from '../../../../../shared/components/toast/toast-objects/ToastType';
import { HttpProjectService } from '../../../../../shared/services/api/http-project.service';
import { ProjectRequest } from '../../../../../shared/model/api/project/ProjectRequest';
import { User } from '../../../../../shared/model/api/user/User';
import { FormStep } from '../../../../../shared/model/app/form/FormStep';
import { FormService } from '../../../../../shared/services/app/form.service';
import { FormField } from '../../../../../shared/model/app/form/FormField';
import { DataType } from '../../../../../shared/model/enums/DataType';

/**
 * Component that display the edit page for a dashboard
 */
@Component({
  selector: 'app-dashboard-edit',
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
        .getOneByToken(params['dashboardToken'])
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
            type: DataType.TEXT,
            value: this.dashboard.name,
            matIconPrefix: 'loyalty',
            validators: [Validators.required, Validators.minLength(3)]
          },
          {
            key: 'token',
            label: translations['token'],
            type: DataType.TEXT,
            value: this.dashboard.token,
            matIconPrefix: 'vpn_key',
            readOnly: true,
            validators: [Validators.required]
          },
          {
            key: 'widgetHeight',
            label: translations['widget.heigth.px'],
            type: DataType.NUMBER,
            value: this.dashboard.gridProperties.widgetHeight,
            matIconPrefix: 'equalizer',
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          },
          {
            key: 'maxColumn',
            label: translations['grid.nb.columns'],
            type: DataType.NUMBER,
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

      this.httpProjectService.editProject(this.dashboard.token, projectRequest).subscribe(() => {
        this.toastService.sendMessage('Dashboard saved successfully', ToastType.SUCCESS);
        this.redirectToDashboardList();
      });
    }
  }

  /**
   * Redirect to dashboard list
   */
  redirectToDashboardList() {
    this.router.navigate(['/dashboards', 'all']);
  }
}
