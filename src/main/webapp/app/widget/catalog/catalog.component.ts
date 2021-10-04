/*
 * Copyright 2012-2021 the original author or authors.
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
import { HttpWidgetService } from '../../shared/services/backend/http-widget/http-widget.service';
import { HttpAssetService } from '../../shared/services/backend/http-asset/http-asset.service';

/**
 * Component used to display the list of widgets
 */
@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class CatalogComponent extends ListComponent<Widget | WidgetRequest> {
  /**
   * Constructor
   *
   * @param httpWidgetService Suricate service used to manage the http calls for widgets
   * @param injector Angular Service used to manage the injection of services
   */
  constructor(private readonly httpWidgetService: HttpWidgetService, protected injector: Injector) {
    super(httpWidgetService, injector);

    this.initHeaderConfiguration();
    this.initFilter();
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
  public getObjectImageURL(widget: Widget): string {
    return HttpAssetService.getContentUrl(widget.imageToken);
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'widget.list'
    };
  }

  /**
   * Init filter for list component
   */
  private initFilter(): void {
    this.httpFilter.sort = ['category.name,name,asc'];
  }
}
