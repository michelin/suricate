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

import { Component, ElementRef, Input, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TitleCasePipe } from '@angular/common';
import { NgGridItemConfig, NgGridItemEvent } from 'angular2-grid';
import * as Stomp from '@stomp/stompjs';
import { SidenavService } from '../../../../shared/services/frontend/sidenav/sidenav.service';
import { DialogService } from '../../../../shared/services/frontend/dialog/dialog.service';
import { ProjectWidgetFormStepsService } from '../../../../shared/services/frontend/form-steps/project-widget-form-steps/project-widget-form-steps.service';
import { IconEnum } from '../../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../../shared/records/material-icon.record';
import { ProjectWidgetRequest } from '../../../../shared/models/backend/project-widget/project-widget-request';
import { ToastService } from '../../../../shared/services/frontend/toast/toast.service';
import { ToastTypeEnum } from '../../../../shared/enums/toast-type.enum';
import { WidgetConfigurationFormFieldsService } from '../../../../shared/services/frontend/form-fields/widget-configuration-form-fields/widget-configuration-form-fields.service';
import { FormGroup } from '@angular/forms';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ProjectWidget } from '../../../../shared/models/backend/project-widget/project-widget';
import { Widget } from '../../../../shared/models/backend/widget/widget';
import { WidgetStateEnum } from '../../../../shared/enums/widget-sate.enum';
import { HttpWidgetService } from '../../../../shared/services/backend/http-widget/http-widget.service';
import { HttpProjectWidgetService } from '../../../../shared/services/backend/http-project-widget/http-project-widget.service';
import { WebsocketService } from '../../../../shared/services/frontend/websocket/websocket.service';
import { LibraryService } from '../../../services/library/library.service';
import { GridItemUtils } from '../../../../shared/utils/grid-item.utils';
import { WebsocketUpdateEvent } from '../../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../../shared/enums/websocket-update-type.enum';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SlideToggleButtonConfiguration } from '../../../../shared/models/frontend/button/slide-toggle/slide-toggle-button-configuration';
import { CategoryParameter } from '../../../../shared/models/backend/category-parameters/category-parameter';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'suricate-dashboard-screen-widget',
  templateUrl: './dashboard-screen-widget.component.html',
  styleUrls: ['./dashboard-screen-widget.component.scss']
})
export class DashboardScreenWidgetComponent implements OnInit, OnDestroy {
  /**
   * The projectWidget to display
   */
  @Input()
  public projectWidget: ProjectWidget;

  /**
   * The grid item config
   */
  @Input()
  public gridStackItem: NgGridItemConfig;

  /**
   * Tell if we are on
   */
  @Input()
  public readOnly: boolean;

  /**
   * The project token
   */
  @Input()
  public projectToken: string;

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * The widget related to this project widget
   */
  public widget: Widget;

  /**
   * The enumeration that hold the state of a widget (used in HTML)
   */
  public widgetStateEnum = WidgetStateEnum;

  /**
   * The configuration of this project widget on the grid
   */
  private startGridStackItem: NgGridItemConfig;

  /**
   * Is the widget loading or not
   */
  public loading = true;

  /**
   * Used to display the buttons when the screen is not readonly
   */
  public displayButtons = false;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param elementRef A reference to the current element
   * @param translateService Front-End service used to manage translations
   * @param httpWidgetService Back-End service used to manage http calls for widgets
   * @param httpProjectWidgetService Back-End service used to manage http calls for project widgets
   * @param websocketService Front-End service used to manage websocket connections
   * @param dialogService Front-End service used to manage dialog
   * @param sidenavService Front-End service used to manage sidenav's
   * @param projectWidgetFormStepsService Front-End service used to generate steps for project widget
   * @param toastService Front-End service used to display messages
   * @param widgetConfigurationFormFieldsService Front-End service used to manage the widget's category settings
   * @param libraryService Front-End service used to manage the libraries
   */
  constructor(
    private readonly elementRef: ElementRef,
    private readonly translateService: TranslateService,
    private readonly httpWidgetService: HttpWidgetService,
    private readonly httpProjectWidgetService: HttpProjectWidgetService,
    private readonly websocketService: WebsocketService,
    private readonly dialogService: DialogService,
    private readonly sidenavService: SidenavService,
    private readonly projectWidgetFormStepsService: ProjectWidgetFormStepsService,
    private readonly toastService: ToastService,
    private readonly widgetConfigurationFormFieldsService: WidgetConfigurationFormFieldsService,
    private readonly libraryService: LibraryService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.initWebsocketConnectionForProjectWidget();
    this.startGridStackItem = { ...this.gridStackItem };

    this.httpWidgetService.getById(this.projectWidget.widgetId).subscribe((widget: Widget) => {
      this.widget = widget;

      this.libraryService.allExternalLibrariesLoaded.subscribe((areExternalLibrariesLoaded: boolean) => {
        this.loading = !areExternalLibrariesLoaded;
      });
    });
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Subscribe to widget events
   */
  private initWebsocketConnectionForProjectWidget(): void {
    const projectWidgetSubscriptionUrl = `/user/${this.projectToken}-projectWidget-${this.projectWidget.id}/queue/live`;

    this.websocketService
      .watch(projectWidgetSubscriptionUrl)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.REFRESH_WIDGET) {
          this.refreshProjectWidget();
        }
      });
  }

  /**
   * Refresh this project widget
   */
  private refreshProjectWidget(): void {
    this.httpProjectWidgetService.getOneById(this.projectWidget.id).subscribe(projectWidget => {
      this.projectWidget = projectWidget;
    });
  }

  /**
   * Register the new position of the element
   *
   * Keep track of the position to prevent the click when moving it
   *
   * @param gridItemEvent The grid item event
   */
  public registerNewPosition(gridItemEvent: NgGridItemEvent): void {
    this.gridStackItem.col = gridItemEvent.col;
    this.gridStackItem.row = gridItemEvent.row;
    this.gridStackItem.sizey = gridItemEvent.sizey;
    this.gridStackItem.sizex = gridItemEvent.sizex;
  }

  /**
   * Disable click event if the item have been moved
   * @param event The click event
   */
  public preventDefault(event: MouseEvent): void {
    if (GridItemUtils.isItemHaveBeenMoved(this.startGridStackItem, this.gridStackItem)) {
      event.preventDefault();
    }
  }

  /**
   * Delete The project widget
   */
  public displayDeleteProjectWidgetDialog(): void {
    const titleCasePipe = new TitleCasePipe();

    this.dialogService.confirm({
      title: 'widget.delete',
      message: `${this.translateService.instant('widget.delete.confirm')} ${titleCasePipe.transform(this.widget.name)} widget ?`,
      accept: () => this.httpProjectWidgetService.deleteOneById(this.projectWidget.id).subscribe()
    });
  }

  /**
   * Display the form sidenav used to edit a project widget
   */
  public displayEditFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'widget.edit',
      formFields: this.projectWidgetFormStepsService.generateWidgetParametersFormFields(
        this.widget.params,
        this.projectWidget.backendConfig
      ),
      save: (formData: FormData) => this.saveWidget(formData),
      slideToggleButtonConfiguration: this.buildSlideToggleButtonConfiguration(this.widget.category.categoryParameters)
    });
  }

  /**
   * Save the widget modifications
   *
   * @param formData The form data
   */
  public saveWidget(formData: FormData) {
    this.loading = true;

    const projectWidgetRequest: ProjectWidgetRequest = {
      widgetId: this.projectWidget.widgetId,
      customStyle: this.projectWidget.customStyle,
      backendConfig: Object.keys(formData)
        .filter((key: string) => formData[key] != null && `${formData[key]}`.trim() !== '')
        .map((key: string) => `${key}=${formData[key].replace(/\n/g, '\\n')}`)
        .join('\n')
    };

    this.httpProjectWidgetService
      .updateOneById(this.projectWidget.id, projectWidgetRequest)
      .subscribe((updatedProjectWidget: ProjectWidget) => {
        this.loading = false;
        this.projectWidget = updatedProjectWidget;
        this.toastService.sendMessage('widget.edit.success', ToastTypeEnum.SUCCESS);
      });
  }

  /**
   * Build the configuration to display the slide toggle button for editing the category of the widget
   *
   * @param categoryParameters The settings of the category
   */
  public buildSlideToggleButtonConfiguration(categoryParameters: CategoryParameter[]): SlideToggleButtonConfiguration {
    return {
      displaySlideToggleButton: categoryParameters.length > 0,
      toggleChecked:
        categoryParameters.filter(categorySetting =>
          this.projectWidgetFormStepsService.retrieveProjectWidgetValueFromConfig(categorySetting.key, this.projectWidget.backendConfig)
        ).length > 0,
      slideToggleButtonPressed: (event: MatSlideToggleChange, formGroup: FormGroup, formFields: FormField[]) =>
        this.widgetConfigurationFormFieldsService.addOrRemoveCategoryParametersFormFields(
          categoryParameters,
          event.checked,
          formGroup,
          formFields,
          this.projectWidget.backendConfig
        )
    };
  }

  /**
   * call the popup that display the execution log
   */
  public displayLogProjectWidgetDialog(): void {
    this.dialogService.info({
      title: 'widget.log',
      message: this.projectWidget.log ? this.projectWidget.log : '',
      isErrorMessage: !!this.projectWidget.log
    });
  }
}
