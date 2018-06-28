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
import {DashboardService} from '../../../dashboard/dashboard.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {User} from '../../../../shared/model/dto/user/User';
import {UserService} from '../../../user/user.service';
import {Observable} from 'rxjs/Observable';
import {empty} from 'rxjs/observable/empty';
import {Project} from '../../../../shared/model/dto/Project';
import {ColorPickerService} from 'ngx-color-picker';

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
   * @param _data The data passed to the dialog
   * @param {FormBuilder} _formBuilder The formbuilder service
   * @param {ChangeDetectorRef} _changeDetectorRef The change detector service
   * @param {DashboardService} _dashboardService The dashboard service
   * @param {UserService} _userService The user service
   * @param {ColorPickerService} _colorPickerService The color picker service for dashboard background color
   */
  constructor(@Inject(MAT_DIALOG_DATA) private _data: any,
              private _formBuilder: FormBuilder,
              private _changeDetectorRef: ChangeDetectorRef,
              private _dashboardService: DashboardService,
              private _userService: UserService,
              private _colorPickerService: ColorPickerService) {
  }

  /**
   * Initialisation of the component
   */
  ngOnInit() {
    if (this._data && this._data.projectId) {
      this.isEditMode = true;
      this._dashboardService
          .getOneById(+this._data.projectId)
          .subscribe(project => {
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
    this.dashboardForm = this._formBuilder.group({
      'name':
          [this.projectAdded ? this.projectAdded.name : '',
            [Validators.required]],
      'widgetHeight':
          [this.projectAdded ? this.projectAdded.widgetHeight : '360',
            [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]],
      'maxColumn':
          [this.projectAdded ? this.projectAdded.maxColumn : '5',
            [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]]
    });
  }

  /**
   * Initialisation of the user form
   */
  private initUserForm() {
    // Init Add user form
    this.addUserForm = this._formBuilder.group({
      'username': ['', [Validators.required]]
    });
    // Populate user autocomplete
    this.userAutoComplete$ = this.addUserForm.get('username').valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(username => username ? this._userService.searchUserByUsername(username) : empty<User[]>())
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
      this.projectAdded.cssStyle = this.getGridCss();

      if (!this.isEditMode) {
        this._dashboardService
            .createProject(this.projectAdded)
            .subscribe(project => this.displayProject(project));

      } else {
        this._dashboardService
            .editProject(this.projectAdded)
            .subscribe(project => this.displayProject(project));
      }
    }
  }

  displayProject(project: Project) {
    this.projectAdded = project;
    this.dashboardFormCompleted = true;
    this._changeDetectorRef.detectChanges();
    this._dashboardService.currentDisplayedDashboardValue = project;
    this.addDashboardStepper.next();
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
    const propertyArray = this.projectAdded.cssStyle.split(';');
    return propertyArray
        .filter((currentProperty: string) => currentProperty.split(':')[0] === property)[0]
        .split(':')[1];
  }

  /**
   * Add a user to the current dashboard
   */
  addUser() {
    if (this.addUserForm.valid) {
      this._dashboardService
          .addUserToProject(this.projectAdded, this.addUserForm.value)
          .subscribe(project => this.projectAdded = project);
    }
  }

  /**
   * Delete a user from the dashboard
   *
   * @param {number} userId The user id
   */
  deleteUser(userId: number) {
    this._dashboardService
        .deleteUserFromProject(this.projectAdded, userId)
        .subscribe(project => this.projectAdded = project);
  }
}
