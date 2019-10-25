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

import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TitleCasePipe } from '@angular/common';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { Observable } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';

import { DashboardService } from '../../../modules/dashboard/dashboard.service';
import { Project } from '../../../shared/models/backend/project/project';
import { User } from '../../../shared/models/backend/user/user';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { HttpUserService } from '../../../shared/services/backend/http-user.service';
import { FormService } from '../../../shared/services/frontend/form.service';
import { FormStep } from '../../../shared/models/frontend/form/form-step';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { TranslateService } from '@ngx-translate/core';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { CustomValidators } from 'ng2-validation';
import { FormChangeEvent } from '../../../shared/models/frontend/form/form-change-event';
import { FormOption } from '../../../shared/models/frontend/form/form-option';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';

@Component({
  selector: 'app-add-dashboard-dialog',
  templateUrl: './add-dashboard-dialog.component.html',
  styleUrls: ['./add-dashboard-dialog.component.scss']
})
export class AddDashboardDialogComponent implements OnInit {
  /**
   * Mat horizontal stepper
   * @type {MatHorizontalStepper}
   */
  @ViewChild('addDashboardStepper', { static: false }) addDashboardStepper: MatHorizontalStepper;
  /**
   * project form group
   * @type {FormGroup}
   */
  projectForm: FormGroup;
  /**
   * Form used to add user
   */
  userForm: FormGroup;
  /**
   * Describe the form
   */
  formSteps: FormStep[];
  /**
   * The current project
   * @type {Project}
   */
  project: Project;
  /**
   * The users of the project
   */
  projectUsers: User[];
  /**
   * The default dashboard background color
   * @type {string}
   */
  dashboardBackgroundColor = '#42424200';

  /**
   * Constructor
   *
   * @param data The data passed to the dialog
   * @param {FormService} formService The form service
   * @param {TranslateService} translateService The translation service
   * @param {Router} router The router service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {HttpProjectService} httpProjectService The project service
   * @param {HttpUserService} httpUserService The http user service to inject
   */
  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    private formService: FormService,
    private translateService: TranslateService,
    private router: Router,
    private dashboardService: DashboardService,
    private httpProjectService: HttpProjectService,
    private httpUserService: HttpUserService
  ) {}

  /**
   * Initialisation of the component
   */
  ngOnInit() {
    if (this.data && this.data.projectToken) {
      this.httpProjectService
        .getOneByToken(this.data.projectToken)
        .pipe(
          map((project: Project) => (this.project = project)),
          flatMap(() => this.httpProjectService.getProjectUsers(this.project.token)),
          map((users: User[]) => (this.projectUsers = users))
        )
        .subscribe(() => {
          this.initForms();
        });
    } else {
      this.initForms();
    }
  }

  /**
   * Init the dashboard form
   */
  private initForms() {
    this.formSteps = [];

    this.generateDashboardStep()
      .pipe(
        map((dashboardStep: FormStep) => (this.formSteps[0] = dashboardStep)),
        flatMap(() => this.generateUserStep()),
        map((userStep: FormStep) => (this.formSteps[1] = userStep))
      )
      .subscribe(() => {
        this.initBackgroundColor();
        this.projectForm = this.formService.generateFormGroupForFields(this.formSteps[0].fields);
        this.userForm = this.formService.generateFormGroupForFields(this.formSteps[1].fields);
      });
  }

  /**
   * Generate the dashboard form step
   */
  generateDashboardStep(): Observable<FormStep> {
    return this.translateService.get(['dashboard.name', 'widget.heigth.px', 'grid.nb.columns']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'name',
            label: translations['dashboard.name'],
            type: DataTypeEnum.TEXT,
            value: this.project ? this.project.name : '',
            validators: [Validators.required]
          },
          {
            key: 'widgetHeight',
            label: translations['widget.heigth.px'],
            type: DataTypeEnum.NUMBER,
            value: this.project ? this.project.gridProperties.widgetHeight : 360,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          },
          {
            key: 'maxColumn',
            label: translations['grid.nb.columns'],
            type: DataTypeEnum.NUMBER,
            value: this.project ? this.project.gridProperties.maxColumn : 5,
            validators: [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]
          }
        ];

        return { fields: formFields, stepCompleted: !!this.project };
      })
    );
  }

  /**
   * Generate the dashboard form step
   */
  generateUserStep(): Observable<FormStep> {
    return this.translateService.get(['username.search']).pipe(
      map((translations: string) => {
        const formFields: FormField[] = [
          {
            key: 'username',
            label: translations['username.search'],
            type: DataTypeEnum.TEXT,
            value: '',
            options: [],
            hint: translations['username.search'],
            matIconPrefix: 'person_pin',
            validators: [Validators.required]
          }
        ];

        return { fields: formFields };
      })
    );
  }

  /**
   * Init the background color property
   */
  initBackgroundColor() {
    let backgroundColor = this.dashboardBackgroundColor;
    if (this.project) {
      backgroundColor = this.getPropertyFromGridCss('background-color');
    }
    this.dashboardBackgroundColor = backgroundColor ? backgroundColor : this.dashboardBackgroundColor;
  }

  /**
   * Catch The event emit when the value as been changed
   *
   * @param event The event of value changed
   */
  catchValueChange(event: FormChangeEvent) {
    if (event.inputKey === 'username') {
      this.httpUserService.getAll(event.value.currentTarget.value).subscribe((users: User[]) => {
        const titleCasePipe = new TitleCasePipe();
        const formField: FormField = this.getFieldByStepNumberAndFieldKey(1, 'username');
        const options: FormOption[] = [];
        if (users) {
          users.forEach((user: User) => {
            options.push({
              key: user.username,
              label: `${titleCasePipe.transform(user.username)} - ${user.firstname} ${user.lastname}`
            });
          });
        }

        formField.options = options;
      });
    }
  }

  /**
   * Get a specifique field for a step and key
   *
   * @param stepNumber The step number
   * @param fieldKey The field key to find
   */
  private getFieldByStepNumberAndFieldKey(stepNumber: number, fieldKey: string) {
    return this.formSteps[1].fields.find((field: FormField) => field.key === fieldKey);
  }

  /**
   * Function used for add/Save a dashboard
   */
  saveDashboard() {
    this.formService.validate(this.projectForm);

    if (this.projectForm.valid) {
      const projectRequest: ProjectRequest = {
        name: this.projectForm.get('name').value,
        maxColumn: this.projectForm.get('maxColumn').value,
        widgetHeight: this.projectForm.get('widgetHeight').value,
        cssStyle: this.getGridCss()
      };

      if (!this.project) {
        this.httpProjectService.createProject(projectRequest).subscribe((project: Project) => {
          this.formSteps[0].stepCompleted = true;
          this.displayProject(project.token);
        });
      } else {
        this.httpProjectService.editProject(this.project.token, projectRequest).subscribe(() => {
          this.displayProject(this.project.token);
        });
      }
    }
  }

  /**
   * Used to display the project informations
   *
   * @param projectToken The project token
   */
  displayProject(projectToken: string) {
    this.httpProjectService
      .getOneByToken(projectToken)
      .pipe(
        map((project: Project) => (this.project = project)),
        flatMap(() => this.httpProjectService.getAllForCurrentUser()),
        map((projects: Project[]) => (this.dashboardService.currentDashboardListValues = projects)),
        flatMap(() => this.httpProjectService.getProjectUsers(projectToken)),
        map((users: User[]) => (this.projectUsers = users))
      )
      .subscribe(() => {
        this.addDashboardStepper.next();
        this.router.navigate(['/dashboards', projectToken]);
      });
  }

  /**
   * Get the Grid css
   *
   * @returns {string} The CSS as string
   */
  private getGridCss(): string {
    return `.grid{
      background-color:${this.dashboardBackgroundColor};
    }`;
  }

  /**
   * Get the CSS property
   *
   * @param {string} property The css property to find
   * @returns {string} The related value
   */
  private getPropertyFromGridCss(property: string): string {
    const propertyArray = [...this.project.gridProperties.cssStyle.split(/[{}]/)].map(value => value.trim());

    const propertyFound = propertyArray.find((currentProperty: string) => {
      return currentProperty.includes(':') && currentProperty.split(':')[0] === property;
    });

    return propertyFound ? propertyFound.split(':')[1].slice(0, -1) : propertyFound;
  }

  /**
   * Add a user to the current dashboard
   */
  addUser() {
    if (this.userForm.valid) {
      this.httpProjectService
        .addUserToProject(this.project.token, this.userForm.value)
        .pipe(
          flatMap(() => this.httpProjectService.getProjectUsers(this.project.token)),
          map((users: User[]) => (this.projectUsers = users))
        )
        .subscribe();
    }
  }

  /**
   * Delete a user from the dashboard
   *
   * @param {number} userId The user id
   */
  deleteUser(userId: number) {
    this.httpProjectService
      .deleteUserFromProject(this.project.token, userId)
      .pipe(
        flatMap(() => this.httpProjectService.getProjectUsers(this.project.token)),
        map((users: User[]) => (this.projectUsers = users))
      )
      .subscribe();
  }
}
