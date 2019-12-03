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

import { Component, Injector } from '@angular/core';
import { ListComponent } from '../../shared/components/list/list.component';
import { IconEnum } from '../../shared/enums/icon.enum';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';
import { HttpWidgetConfigurationService } from '../../shared/services/backend/http-widget-configuration.service';
import { WidgetConfiguration } from '../../shared/models/backend/widget-configuration/widget-configuration';
import { WidgetConfigurationFormFieldsService } from '../../shared/form-fields/widget-configuration-form-fields.service';

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class WidgetConfigurationsComponent extends ListComponent<WidgetConfiguration> {
  /**
   * The item selected on the list
   */
  private configurationSelected: WidgetConfiguration;

  /**
   * Constructor
   *
   * @param httpConfigurationsService Suricate service used to manage http calls for configuration
   * @param widgetConfigurationFormFieldsService Frontend service used to build form fields for project configuration
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(
    private readonly httpConfigurationsService: HttpWidgetConfigurationService,
    private readonly widgetConfigurationFormFieldsService: WidgetConfigurationFormFieldsService,
    protected injector: Injector
  ) {
    super(httpConfigurationsService, injector);

    this.initHeaderConfiguration();
    this.initListConfiguration();
    this.initFilter();
  }

  /**
   * {@inheritDoc}
   */
  protected getFirstLabel(configuration: WidgetConfiguration): string {
    return configuration.key;
  }

  /**
   * {@inheritDoc}
   */
  protected getSecondLabel(configuration: WidgetConfiguration): string {
    return configuration.value;
  }

  /**
   * {@inheritDoc}
   */
  protected getThirdLabel(configuration: WidgetConfiguration): string {
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
          icon: IconEnum.EDIT,
          color: 'primary',
          callback: (event: Event, configuration: WidgetConfiguration) =>
            this.openFormSidenav(event, configuration, this.updateConfiguration.bind(this))
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          callback: (event: Event, configuration: WidgetConfiguration) => this.deleteConfiguration(event, configuration)
        }
      ]
    };
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['key,asc'];
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
    configuration: WidgetConfiguration,
    saveCallback: (configuration: WidgetConfiguration) => void
  ): void {
    this.configurationSelected = configuration ? Object.assign({}, configuration) : new WidgetConfiguration();

    this.sidenavService.openFormSidenav({
      title: 'configuration.edit',
      formFields: this.widgetConfigurationFormFieldsService.generateFormFields(configuration),
      save: (configurationRequest: WidgetConfiguration) => saveCallback(configurationRequest)
    });
  }

  /**
   * Function used to delete a project
   *
   * @param event The click event
   * @param configuration The project to delete
   */
  private deleteConfiguration(event: Event, configuration: WidgetConfiguration): void {
    this.dialogService.confirm({
      title: 'configuration.delete',
      message: `${this.translateService.instant('delete.confirm')} ${configuration.key.toUpperCase()} ?`,
      accept: () => {
        this.httpConfigurationsService.delete(configuration.key).subscribe(() => {
          this.toastService.sendMessage('configuration.delete.success', ToastTypeEnum.SUCCESS);
          this.refreshList();
        });
      }
    });
  }

  /**
   * Update a configuration
   *
   * @param configuration The configuration to update
   */
  private updateConfiguration(configuration: WidgetConfiguration): void {
    this.httpConfigurationsService.update(configuration.key, configuration).subscribe(() => {
      this.refreshList();
      this.toastService.sendMessage('configuration.update.success', ToastTypeEnum.SUCCESS);
    });
  }
}
