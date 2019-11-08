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
import { Widget } from '../../shared/models/backend/widget/widget';
import { WidgetRequest } from '../../shared/models/backend/widget/widget-request';
import { AuthenticationService } from '../../shared/services/frontend/authentication.service';
import { HttpWidgetService } from '../../shared/services/backend/http-widget.service';
import { HttpAssetService } from '../../shared/services/backend/http-asset.service';
import { ApiActionEnum } from '../../shared/enums/api-action.enum';
import { ToastTypeEnum } from '../../shared/enums/toast-type.enum';

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class WidgetsComponent extends ListComponent<Widget | WidgetRequest> {
  /**
   * Project selected in the list for modification
   */
  private widgetSelected: Widget;
  /**
   * Used to know if the current user is admin
   */
  public isAdmin = AuthenticationService.isAdmin();

  /**
   * Constructor
   *
   * @param httpWidgetService Suricate service used to manage the http calls for widgets
   * @param httpAssetService Suricate service used to manage the assets
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(
    private readonly httpWidgetService: HttpWidgetService,
    private readonly httpAssetService: HttpAssetService,
    protected injector: Injector
  ) {
    super(httpWidgetService, injector);

    this.initHeaderConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'widgets',
      actions: [
        {
          icon: IconEnum.REFRESH,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'Import widgets' },
          callback: () => this.importWidgets()
        }
      ]
    };
  }

  /**
   * Used to ré-imports the widgets
   */
  private importWidgets(): void {
    this.httpWidgetService.getAll(null, ApiActionEnum.REFRESH).subscribe(() => {
      this.toastService.sendMessage('Widget successfully imported', ToastTypeEnum.SUCCESS);
    });
  }

  /**
   * {@inheritDoc}
   */
  protected getFirstLabel(widget: Widget): string {
    return widget.name;
  }

  /**
   * {@inheritDoc}
   */
  protected getSecondLabel(widget: Widget): string {
    return widget.description;
  }

  /**
   * {@inheritDoc}
   */
  protected getThirdLabel(widget: Widget): string {
    return widget.category.name;
  }

  /**
   * {@inheritDoc}
   */
  protected getObjectImageURL(widget: Widget): string {
    return this.httpAssetService.getContentUrl(widget.imageToken);
  }
}
