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

import {Component} from '@angular/core';
import {ToastService} from '../../../../shared/components/toast/toast.service';
import {ToastType} from '../../../../shared/components/toast/toast-objects/ToastType';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {ApiActionEnum} from '../../../../shared/model/enums/ApiActionEnum';

/**
 * Hold the widget list actions
 */
@Component({
  selector: 'app-widget-list-actions',
  templateUrl: './widget-list-actions.component.html',
  styleUrls: ['./widget-list-actions.component.css']
})
export class WidgetListActionsComponent {

  /**
   * Constructor
   *
   * @param {HttpWidgetService} httpWidgetService The http widget service
   * @param {ToastService} toastService The toast service
   */
  constructor(private httpWidgetService: HttpWidgetService,
              private toastService: ToastService) {
  }

  /**
   * Method used to reload widgets from repository
   */
  reloadWidgets() {
    this.httpWidgetService.getAll(ApiActionEnum.REFRESH).subscribe(() => {
      this.toastService.sendMessage('Widget successfully reloads', ToastType.SUCCESS);
    });
  }

}
