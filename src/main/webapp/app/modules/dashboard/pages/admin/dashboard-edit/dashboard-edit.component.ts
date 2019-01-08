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

import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {CustomValidators} from 'ng2-validation';

import {Project} from '../../../../../shared/model/api/project/Project';
import {DashboardService} from '../../../dashboard.service';
import {ToastService} from '../../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../../shared/components/toast/toast-objects/ToastType';
import {HttpProjectService} from '../../../../../shared/services/api/http-project.service';
import {ProjectRequest} from '../../../../../shared/model/api/project/ProjectRequest';
import {User} from '../../../../../shared/model/api/user/User';

/**
 * Component that display the edit page for a dashboard
 */
@Component({
  selector: 'app-dashboard-edit',
  templateUrl: './dashboard-edit.component.html',
  styleUrls: ['./dashboard-edit.component.css']
})
export class DashboardEditComponent implements OnInit {

  /**
   * The dashboard form
   * @type {FormGroup}
   */
  editDashboardForm: FormGroup;

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
   * @param {DashboardService} dashboardService The dashboard service to inject
   * @param {HttpProjectService} httpProjectService The http project service to inject
   * @param {ActivatedRoute} activatedRoute The activated route to inject
   * @param {FormBuilder} formBuilder The formBuilder service
   * @param {ToastService} toastService The service used for displayed Toast notification
   */
  constructor(private dashboardService: DashboardService,
              private httpProjectService: HttpProjectService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private toastService: ToastService) {
  }

  /**
   * Called when the component is displayed
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.httpProjectService.getOneByToken(params['dashboardToken']).subscribe(dashboard => {
        this.dashboard = dashboard;
        this.initDashboardForm();

        this.httpProjectService.getProjectUsers(params['dashboardToken']).subscribe(users => {
          this.dashboardUsers = users;
        });
      });
    });
  }

  /**
   * Init the dashboard edit form
   */
  initDashboardForm() {
    this.editDashboardForm = this.formBuilder.group({
      name: [this.dashboard.name, [Validators.required, Validators.minLength(3)]],
      token: [this.dashboard.token, [Validators.required]],
      widgetHeight: [this.dashboard.gridProperties.widgetHeight, [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]],
      maxColumn: [this.dashboard.gridProperties.maxColumn, [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]]
    });
  }


  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.editDashboardForm.invalid && (this.editDashboardForm.get(field).dirty || this.editDashboardForm.get(field).touched);
  }

  /**
   * edit the dashboard
   */
  saveDashboard() {
    const projectRequest: ProjectRequest = {...this.dashboard, ...this.editDashboardForm.value};

    this.httpProjectService.editProject(this.dashboard.token, projectRequest).subscribe(() => {
      this.toastService.sendMessage('Dashboard saved successfully', ToastType.SUCCESS);
    });
  }
}
