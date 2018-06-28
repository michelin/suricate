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
import {ConfigurationService} from '../configuration.service';
import {ActivatedRoute} from '@angular/router';
import {Configuration} from '../../../shared/model/dto/Configuration';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastService} from '../../../shared/components/toast/toast.service';
import {ToastType} from '../../../shared/model/toastNotification/ToastType';
import {ConfigurationDataType} from '../../../shared/model/dto/enums/ConfigurationDataType';

/**
 * Manage the edition of a configuration
 */
@Component({
  selector: 'app-configuration-edit',
  templateUrl: './configuration-edit.component.html',
  styleUrls: ['./configuration-edit.component.css']
})
export class ConfigurationEditComponent implements OnInit {

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
   * @param {ActivatedRoute} _activatedRoute The activated route service
   * @param {FormBuilder} _formBuilder The form builder
   * @param {ToastService} _toastService The toast service
   * @param {ConfigurationService} _configurationService The configuration service
   */
  constructor(private _activatedRoute: ActivatedRoute,
              private _formBuilder: FormBuilder,
              private _toastService: ToastService,
              private _configurationService: ConfigurationService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this._activatedRoute.params.subscribe(params => {
      this._configurationService.getOneByKey(params['configurationKey']).subscribe(configuration => {
        this.configuration = configuration;
        this.initConfigForm();
      });
    });
  }

  /**
   * Init the configuration form
   */
  initConfigForm() {
    this.configurationForm = this._formBuilder.group({
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

      this._configurationService
          .updateConfigurationByKey(this.configuration)
          .subscribe(() => {
            this._toastService.sendMessage('Configuration updated successfully', ToastType.SUCCESS);
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
