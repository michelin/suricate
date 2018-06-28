import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Project} from '../../../shared/model/dto/Project';
import {DashboardService} from '../dashboard.service';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../../../shared/components/toast/toast.service';
import {CustomValidators} from 'ng2-validation';
import {ToastType} from '../../../shared/model/toastNotification/ToastType';

@Component({
  selector: 'app-dashboard-edit',
  templateUrl: './dashboard-edit.component.html',
  styleUrls: ['./dashboard-edit.component.css']
})
export class DashboardEditComponent implements OnInit {

  /**
   * The dashboard form
   */
  editDashboardForm: FormGroup;

  /**
   * The dashbaord to edit
   */
  dashboard: Project;

  /**
   * Constructor
   *
   * @param {DashboardService} _dashboardService The dashboard service to inject
   * @param {ActivatedRoute} _activatedRoute The activated route to inject
   * @param {FormBuilder} _formBuilder The formBuilder service
   * @param {ToastService} _toastService The service used for displayed Toast notification
   */
  constructor(private _dashboardService: DashboardService,
              private _activatedRoute: ActivatedRoute,
              private _formBuilder: FormBuilder,
              private _toastService: ToastService) {
  }

  /**
   * Called when the component is displayed
   */
  ngOnInit() {
    this
        ._activatedRoute
        .params
        .subscribe(params => {
          this
              ._dashboardService
              .getOneById(+params['dashboardId'])
              .subscribe(dashboard => {
                this.dashboard = dashboard;
                this.initDashboardForm();
              });
        });
  }

  /**
   * Init the dashboard edit form
   */
  initDashboardForm() {
    this.editDashboardForm = this._formBuilder.group({
      name: [this.dashboard.name, [Validators.required, Validators.minLength(3)]],
      token: [this.dashboard.token, [Validators.required]],
      widgetHeight: [this.dashboard.widgetHeight, [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]],
      maxColumn: [this.dashboard.maxColumn, [Validators.required, CustomValidators.digits, CustomValidators.gt(0)]]
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
    this
        ._dashboardService
        .editProject({...this.dashboard, ...this.editDashboardForm.value})
        .subscribe(() => this._toastService.sendMessage('Dashboard saved successfully', ToastType.SUCCESS));
  }
}
