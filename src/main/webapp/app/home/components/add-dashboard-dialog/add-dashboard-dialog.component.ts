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

import {ChangeDetectorRef, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatHorizontalStepper} from '@angular/material';
import {CustomValidators} from 'ng2-validation';
import {ColorPickerService} from 'ngx-color-picker';
import {Observable} from 'rxjs';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';

import {DashboardService} from '../../../modules/dashboard/dashboard.service';
import {UserService} from '../../../modules/security/user/user.service';
import {Project} from '../../../shared/model/api/project/Project';
import {User} from '../../../shared/model/api/user/User';
import {HttpProjectService} from '../../../shared/services/api/http-project.service';
import {HttpUserService} from '../../../shared/services/api/http-user.service';
import {ProjectRequest} from '../../../shared/model/api/project/ProjectRequest';

@Component({
  selector: 'app-add-dashboard-dialog',
  templateUrl: './add-dashboard-dialog.component.html',
  styleUrls: ['./add-dashboard-dialog.component.css']
})
export class AddDashboardDialogComponent implements OnInit {
  /**
   * Mat horizontal stepper
   * @type {MatHorizontalStepper}
   */
  @ViewChild('addDashboardStepper') addDashboardStepper: MatHorizontalStepper;

  /**
   * Dashboard form group
   * @type {FormGroup}
   */
  dashboardForm: FormGroup;

  /**
   * Tell if the form has been completed or not
   * @type {boolean}
   */
  dashboardFormCompleted: boolean;

  /**
   * User form group
   * @type {FormGroup}
   */
  addUserForm: FormGroup;
  /**
   * Observable of users (Used for auto completion
   * @type {Observable<User[]>}
   */
  userAutoComplete$: Observable<User[]>;
  /**
   * The current project
   * @type {Project}
   */
  projectAdded: Project;

  /**
   * Tel if we open the dialog in edit mode or not
   * @type {boolean}
   */
  isEditMode = false;

  /**
   * The default dashboard background color
   * @type {string}
   */
  dashboardBackgroundColor = '#42424200';

  /**
   * Constructor
   *
   * @param data The data passed to the dialog
   * @param {FormBuilder} formBuilder The formbuilder service
   * @param {ChangeDetectorRef} changeDetectorRef The change detector service
   * @param {DashboardService} dashboardService The dashboard service
   * @param {HttpProjectService} httpProjectService The project service
   * @param {HttpUserService} httpUserService The http user service to inject
   * @param {UserService} userService The user service
   * @param {ColorPickerService} colorPickerService The color picker service for dashboard background color
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef,
              private dashboardService: DashboardService,
              private httpProjectService: HttpProjectService,
              private httpUserService: HttpUserService,
              private userService: UserService,
              private colorPickerService: ColorPickerService) {
  }

  /**
   * Initialisation of the component
   */
  ngOnInit() {
    if (this.data && this.data.projectId) {
      this.isEditMode = true;

      this.httpProjectService.getOneByToken(this.data.projectId).subscribe(project => {
        this.projectAdded = project;
        this.dashboardBackgroundColor = this.getPropertyFromGridCss('background-color');
        this.initDashboardForm(true);
        this.initUserForm();
      });

    } else {
      // init form dashboard
      this.initDashboardForm(false);

      // Init user form
      this.initUserForm();
    }
  }

  /**
   * Init the dashboard form
   *
   * @param {boolean} formCompleted True if the form has been completed, false otherwise
   */
  private initDashboardForm(formCompleted: boolean) {
    this.dashboardFormCompleted = formCompleted;
    this.dashboardForm = this.formBuilder.group({
      'name': [this.projectAdded ? this.projectAdded.name : '', [Validators.required]],
      'widgetHeight': [this.projectAdded ? this.projectAdded.gridProperties.widgetHeight : '360', [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]],
      'maxColumn': [this.projectAdded ? this.projectAdded.gridProperties.maxColumn : '5', [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]]
    });
  }

  /**
   * Initialisation of the user form
   */
  private initUserForm() {
    // Init Add user form
    this.addUserForm = this.formBuilder.group({
      'username': ['', [Validators.required]]
    });
    // Populate user autocomplete
    this.userAutoComplete$ = this.addUserForm.get('username').valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(username => username ? this.httpUserService.getAll(username) : new Observable<User[]>())
    );
  }

  /**
   * Check if the field is valid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field is valid, false otherwise
   */
  isFieldInvalid(field: string) {
    return this.dashboardForm.invalid && (this.dashboardForm.get(field).dirty || this.dashboardForm.get(field).touched);
  }

  /**
   * Function used for add/Save a dashboard
   */
  saveDashboard() {
    if (this.dashboardForm.valid) {
      this.projectAdded = {
        ...this.projectAdded,
        ...this.dashboardForm.value
      };
      this.projectAdded.gridProperties.cssStyle = this.getGridCss();

      if (!this.isEditMode) {
        this.httpProjectService.createProject(this.getProjectRequestFromProject(this.projectAdded)).subscribe((project: Project) => {
          this.displayProject(project.token);
        });

      } else {
        this.httpProjectService.editProject(this.projectAdded.token, this.getProjectRequestFromProject(this.projectAdded)).subscribe(() => {
          this.displayProject(this.projectAdded.token);
        });
      }
    }
  }

  displayProject(projectToken: string) {
    this.httpProjectService.getOneByToken(projectToken).subscribe((project: Project) => {
      this.projectAdded = project;
      this.dashboardFormCompleted = true;
      this.changeDetectorRef.detectChanges();
      this.dashboardService.currentDisplayedDashboardValue = project;
      this.addDashboardStepper.next();
    });
  }

  /**
   * Get the Grid css
   *
   * @returns {string} The CSS as string
   */
  private getGridCss(): string {
    return `background-color:${this.dashboardBackgroundColor};`;
  }

  /**
   * Get the CSS property
   *
   * @param {string} property The css property to find
   * @returns {string} The related value
   */
  private getPropertyFromGridCss(property: string): string {
    const propertyArray = this.projectAdded.gridProperties.cssStyle.split(';');
    return propertyArray
      .filter((currentProperty: string) => currentProperty.split(':')[0] === property)[0]
      .split(':')[1];
  }

  /**
   * Add a user to the current dashboard
   */
  addUser() {
    if (this.addUserForm.valid) {
      this.httpProjectService.addUserToProject(this.projectAdded.token, this.addUserForm.value).subscribe();
    }
  }

  /**
   * Delete a user from the dashboard
   *
   * @param {number} userId The user id
   */
  deleteUser(userId: number) {
    this.httpProjectService.deleteUserFromProject(this.projectAdded.token, userId).subscribe();
  }

  getProjectRequestFromProject(project: Project): ProjectRequest {
    return {
      name: project.name,
      maxColumn: project.gridProperties.maxColumn,
      widgetHeight: project.gridProperties.widgetHeight,
      cssStyle: project.gridProperties.cssStyle
    };
  }
}
