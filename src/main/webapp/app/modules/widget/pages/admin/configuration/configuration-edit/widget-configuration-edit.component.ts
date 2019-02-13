/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';

import {HttpConfigurationService} from '../../../../../../shared/services/api/http-configuration.service';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';
import {Configuration} from '../../../../../../shared/model/api/configuration/Configuration';
import {ToastType} from '../../../../../../shared/components/toast/toast-objects/ToastType';
import {DataType} from '../../../../../../shared/model/enums/DataType';

/**
 * Manage the edition of a configuration
 */
@Component({
  selector: 'app-widget-configuration-edit',
  templateUrl: './widget-configuration-edit.component.html',
  styleUrls: ['./widget-configuration-edit.component.scss']
})
export class WidgetConfigurationEditComponent implements OnInit {

  /**
   * The edit form
   * @type {FormGroup}
   */
  configurationForm: FormGroup;

  /**
   * The current configuration
   * @type {Configuration}
   */
  configuration: Configuration;

  /**
   * The configuration data type
   * @type {DataType}
   */
  configurationDataType = DataType;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {Router} router The router service to inject
   * @param {FormBuilder} formBuilder The form builder
   * @param {ToastService} toastService The toast service
   * @param {HttpConfigurationService} configurationService The configuration service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private toastService: ToastService,
              private configurationService: HttpConfigurationService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.configurationService.getOneByKey(params['configurationKey']).subscribe(configuration => {
        this.configuration = configuration;
        this.initConfigForm();
      });
    });
  }

  /**
   * Init the configuration form
   */
  initConfigForm() {
    this.configurationForm = this.formBuilder.group({
      key: [this.configuration.key, [Validators.required]],
      value: [this.configuration.value ? this.configuration.value : '', [Validators.required]],
      category: [this.configuration.category.name, [Validators.required]]
    });
  }

  /**
   * Save the configuration
   */
  saveConfiguration() {
    if (this.configurationForm.valid) {
      const configuration = this.configuration;
      configuration.value = this.configurationForm.get('value').value;

      this.configurationService.updateConfigurationByKey(configuration.key, this.configuration).subscribe(() => {
        this.toastService.sendMessage('Configuration updated successfully', ToastType.SUCCESS);
        this.redirectToWidgetConfigurationList();
      });
    }
  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.configurationForm.invalid && (this.configurationForm.get(field).dirty || this.configurationForm.get(field).touched);
  }

  /**
   * Redirect to widget configuration list when editing succesfully
   */
  redirectToWidgetConfigurationList() {
    this.router.navigate(['/widgets/configurations']);
  }
}
