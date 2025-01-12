/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { ListComponent } from '../../shared/components/list/list.component';
import { DataTypeEnum } from '../../shared/enums/data-type.enum';
import { IconEnum } from '../../shared/enums/icon.enum';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { CategoryParameter } from '../../shared/models/backend/category-parameters/category-parameter';
import { WidgetConfigurationRequest } from '../../shared/models/backend/widget-configuration/widget-configuration-request';
import { HttpCategoryParametersService } from '../../shared/services/backend/http-category-parameters/http-category-parameters.service';
import { WidgetConfigurationFormFieldsService } from '../../shared/services/frontend/form-fields/widget-configuration-form-fields/widget-configuration-form-fields.service';

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class ConfigurationsComponent extends ListComponent<CategoryParameter, WidgetConfigurationRequest> {
  /**
   * Constructor
   *
   * @param httpCategoryParametersService Suricate service used to manage http calls for category parameters
   * @param widgetConfigurationFormFieldsService Frontend service used to build form fields for project configuration
   */
  constructor(
    private readonly httpCategoryParametersService: HttpCategoryParametersService,
    private readonly widgetConfigurationFormFieldsService: WidgetConfigurationFormFieldsService
  ) {
    super(httpCategoryParametersService);

    this.initHeaderConfiguration();
    this.initListConfiguration();
    this.initFilter();
  }

  /**
   * {@inheritDoc}
   */
  protected override getFirstLabel(configuration: CategoryParameter): string {
    return configuration.description;
  }

  /**
   * {@inheritDoc}
   */
  protected override getSecondLabel(configuration: CategoryParameter): string {
    return configuration.value && configuration.dataType === DataTypeEnum.PASSWORD && !configuration.showValue
      ? '•'.repeat(configuration.value.length)
      : configuration.value;
  }

  /**
   * {@inheritDoc}
   */
  protected override getThirdLabel(configuration: CategoryParameter): string {
    return configuration.category.name;
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'widget.configuration.list'
    };
  }

  /**
   * Function used to init the configuration of the list
   */
  private initListConfiguration(): void {
    this.listConfiguration = {
      buttons: [
        {
          icon: IconEnum.SHOW_PASSWORD,
          tooltip: { message: 'configuration.show.password' },
          color: 'primary',
          variant: 'miniFab',
          hidden: (configuration: CategoryParameter) =>
            !configuration.value || configuration.dataType !== DataTypeEnum.PASSWORD || configuration.showValue,
          callback: (event: Event, configuration: CategoryParameter) => (configuration.showValue = true)
        },
        {
          icon: IconEnum.HIDE_PASSWORD,
          tooltip: { message: 'configuration.hide.password' },
          color: 'primary',
          variant: 'miniFab',
          hidden: (configuration: CategoryParameter) =>
            !configuration.value || configuration.dataType !== DataTypeEnum.PASSWORD || !configuration.showValue,
          callback: (event: Event, configuration: CategoryParameter) => (configuration.showValue = false)
        },
        {
          icon: IconEnum.EDIT,
          tooltip: { message: 'configuration.edit' },
          color: 'primary',
          variant: 'miniFab',
          callback: (event: Event, configuration: CategoryParameter) =>
            this.openFormSidenav(event, configuration, this.updateConfiguration.bind(this))
        },
        {
          icon: IconEnum.DELETE,
          tooltip: { message: 'configuration.delete' },
          color: 'warn',
          variant: 'miniFab',
          callback: (event: Event, configuration: CategoryParameter) => this.deleteConfiguration(event, configuration)
        }
      ]
    };
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['category.name,description,asc'];
  }

  /**
   * Open the form sidenav
   *
   * @param event The click event
   * @param configuration The repository clicked on the list
   * @param saveCallback The function to call when save button is clicked
   */
  private openFormSidenav(
    event: Event,
    configuration: CategoryParameter,
    saveCallback: (formGroup: UntypedFormGroup) => void
  ): void {
    this.sidenavService.openFormSidenav({
      title: 'configuration.edit',
      formFields: this.widgetConfigurationFormFieldsService.generateFormFields(configuration),
      save: (formGroup: UntypedFormGroup) => saveCallback(formGroup)
    });
  }

  /**
   * Function used to delete a project
   *
   * @param event The click event
   * @param configuration The project to delete
   */
  private deleteConfiguration(event: Event, configuration: CategoryParameter): void {
    this.dialogService.confirm({
      title: 'configuration.delete',
      message: `${this.translateService.instant('configuration.delete.confirm')} ${configuration.key.toUpperCase()} ?`,
      accept: () => {
        this.httpCategoryParametersService.delete(configuration.key).subscribe(() => {
          this.toastService.sendMessage('configuration.delete.success', ToastTypeEnum.SUCCESS);
          this.refreshList();
        });
      }
    });
  }

  /**
   * Update a configuration
   *
   * @param formGroup The form group
   */
  private updateConfiguration(formGroup: UntypedFormGroup): void {
    this.httpCategoryParametersService.update(formGroup.value.key, formGroup.value).subscribe(() => {
      this.refreshList();
      this.toastService.sendMessage('configuration.update.success', ToastTypeEnum.SUCCESS);
    });
  }
}
