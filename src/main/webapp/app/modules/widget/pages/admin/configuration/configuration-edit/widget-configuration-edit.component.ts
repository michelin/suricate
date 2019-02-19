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
import {FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';

import {HttpConfigurationService} from '../../../../../../shared/services/api/http-configuration.service';
import {ToastService} from '../../../../../../shared/components/toast/toast.service';
import {Configuration} from '../../../../../../shared/model/api/configuration/Configuration';
import {ToastType} from '../../../../../../shared/components/toast/toast-objects/ToastType';
import {DataType} from '../../../../../../shared/model/enums/DataType';
import {FormField} from '../../../../../../shared/model/app/form/FormField';
import {map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {FormService} from '../../../../../../shared/services/app/form.service';
import {Observable} from 'rxjs';

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
   * Object used to describe the form
   * @type {FormField[]}
   */
  formFields: FormField[];

  /**
   * The current configuration
   * @type {Configuration}
   */
  configuration: Configuration;

  /**
   * The configuration data type
   * @type {DataType}
   */
  dataType = DataType;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} activatedRoute The activated route service
   * @param {Router} router The router service to inject
   * @param {ToastService} toastService The toast service
   * @param {HttpConfigurationService} configurationService The configuration service
   * @param {TranslateService} translateService The translation service
   * @param {FormService} formService The form service to inject
   */
  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private toastService: ToastService,
              private configurationService: HttpConfigurationService,
              private translateService: TranslateService,
              private formService: FormService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.configurationService.getOneByKey(params['configurationKey']).pipe(
        map((configuration: Configuration) => this.configuration = configuration)
      ).subscribe(() => {
        this.initConfigForm();
      });
    });
  }

  /**
   * Init the configuration form
   */
  initConfigForm() {
    this.generateFormFields().pipe(
      map((formFields: FormField[]) => this.formFields = formFields)
    ).subscribe(() => {
      this.configurationForm = this.formService.generateFormGroupForFields(this.formFields);
    });
  }

  generateFormFields(): Observable<FormField[]> {
    return this.translateService.get(['key', 'configuration.category', 'value']).pipe(
      map((translations: string) => {
        return [
          {
            key: 'key',
            label: translations['key'],
            type: DataType.TEXT,
            value: this.configuration.key,
            readOnly: true,
            matIconPrefix: 'vpn_key',
            validators: [Validators.required]
          },
          {
            key: 'category',
            label: translations['configuration.category'],
            type: DataType.TEXT,
            value: this.configuration.category.name,
            readOnly: true,
            matIconPrefix: 'widgets',
            validators: [Validators.required]
          },
          {
            key: 'value',
            label: translations['value'],
            type: this.configuration.dataType,
            value: this.configuration.value,
            matIconPrefix: 'input',
            validators: [Validators.required]
          }
        ];
      })
    );
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
