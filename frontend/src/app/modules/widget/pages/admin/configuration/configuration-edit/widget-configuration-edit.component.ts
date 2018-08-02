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
import {ActivatedRoute} from '@angular/router';

import {WidgetConfigurationService} from '../widget-configuration.service';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';
import {Configuration} from '../../../../../../shared/model/dto/Configuration';
import {ConfigurationDataType} from '../../../../../../shared/model/dto/enums/ConfigurationDataType';
import {ToastType} from '../../../../../../shared/model/toastNotification/ToastType';

/**
 * Manage the edition of a configuration
 */
@Component({
  selector: 'app-widget-configuration-edit',
  templateUrl: './widget-configuration-edit.component.html',
  styleUrls: ['./widget-configuration-edit.component.css']
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
   * @type {ConfigurationDataType}
   */
  configurationDataType = ConfigurationDataType;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {FormBuilder} formBuilder The form builder
   * @param {ToastService} toastService The toast service
   * @param {WidgetConfigurationService} configurationService The configuration service
   */
  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private toastService: ToastService,
              private configurationService: WidgetConfigurationService) {
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

      this.configurationService
          .updateConfigurationByKey(this.configuration)
          .subscribe(() => {
            this.toastService.sendMessage('Configuration updated successfully', ToastType.SUCCESS);
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

}
