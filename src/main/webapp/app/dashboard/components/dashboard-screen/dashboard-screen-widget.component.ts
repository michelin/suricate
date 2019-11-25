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

import { Component, ElementRef, HostBinding, Input, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TitleCasePipe } from '@angular/common';
import { NgGridItemConfig, NgGridItemEvent } from 'angular2-grid';
import { takeWhile, tap } from 'rxjs/operators';

import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { Widget } from '../../../shared/models/backend/widget/widget';
import { HttpWidgetService } from '../../../shared/services/backend/http-widget.service';
import { WidgetStateEnum } from '../../../shared/enums/widget-sate.enum';
import { HttpProjectWidgetService } from '../../../shared/services/backend/http-project-widget.service';
import { RunScriptsDirective } from '../../../shared/directives/run-scripts.directive';
import { WebsocketService } from '../../../shared/services/frontend/websocket.service';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { GridItemUtils } from '../../../shared/utils/grid-item.utils';

import * as Stomp from '@stomp/stompjs';
import { SidenavService } from '../../../shared/services/frontend/sidenav.service';
import { DialogService } from '../../../shared/services/frontend/dialog.service';
import { ProjectWidgetFormStepsService } from '../../../shared/form-steps/project-widget-form-steps.service';

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
   * @type {ProjectWidget}
   * @public
   */
  @Input()
  public projectWidget: ProjectWidget;
  /**
   * The grid item config
   * @type {NgGridItemConfig}
   * @public
   */
  @Input()
  public gridStackItem: NgGridItemConfig;
  /**
   * Tell if we are on
   * @type {boolean}
   * @public
   */
  @Input()
  public readOnly: boolean;
  /**
   * The project token
   * @type {string}
   * @public
   */
  @Input()
  public projectToken: string;
  /**
   * Add runScriptsDirective, so we can recall it
   * @type {RunScriptsDirective}
   * @public
   */
  @HostBinding('attr.appRunScripts')
  public appRunScriptDirective = new RunScriptsDirective(this.elementRef);

  /**
   * The widget related to this project widget
   * @type {Widget}
   * @protected
   */
  protected widget: Widget;
  /**
   * The enumeration that hold the state of a widget (used in HTML)
   * @type {widgetStateEnum}
   * @protected
   */
  protected widgetStateEnum = WidgetStateEnum;
  /**
   * True when the component is alive
   * @type {boolean}
   * @private
   */
  private isAlive = true;
  /**
   * The configuration of this project widget on the grid
   * @type {NgGridItemConfig}
   * @private
   */
  private startGridStackItem: NgGridItemConfig;
  /**
   * Tell if the component is Loading widget
   * @type {boolean}
   * @protected
   */
  protected isComponentLoading = true;
  /**
   * Used to display the buttons when the screen is not readonly
   * @type {boolean}
   * @protected
   */
  protected displayButtons = false;

  /**
   * Constructor
   *
   * @param {ElementRef} elementRef Angular service used to inject a reference on the component
   * @param {TranslateService} translateService NgxTranslate service used to manage translations
   * @param {HttpWidgetService} httpWidgetService Suricate service used to manage http calls for widgets
   * @param {HttpProjectWidgetService} httpProjectWidgetService Suricate service used to manage http calls for project widgets
   * @param {WebsocketService} websocketService Frontend service used to manage websocket connections
   * @param {DialogService} dialogService Frontend service used to manage dialog
   * @param {SidenavService} sidenavService Frontend service used to manage sidenav's
   */
  constructor(
    private readonly elementRef: ElementRef,
    private readonly translateService: TranslateService,
    private readonly httpWidgetService: HttpWidgetService,
    private readonly httpProjectWidgetService: HttpProjectWidgetService,
    private readonly websocketService: WebsocketService,
    private readonly dialogService: DialogService,
    private readonly sidenavService: SidenavService,
    private readonly projectWidgetFormStepsService: ProjectWidgetFormStepsService
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.initWebsocketConnectionForProjectWidget();
    this.startGridStackItem = { ...this.gridStackItem };

    this.httpWidgetService
      .getById(this.projectWidget.widgetId)
      .pipe(tap((widget: Widget) => (this.widget = widget)))
      .subscribe(() => {
        this.isComponentLoading = false;
        setTimeout(() => this.appRunScriptDirective.ngOnInit(), 100);
      });
  }

  /**
   * Register the new posisition of the element
   *
   * @param gridItemEvent The grid item event
   */
  protected registerNewPosition(gridItemEvent: NgGridItemEvent): void {
    this.gridStackItem.col = gridItemEvent.col;
    this.gridStackItem.row = gridItemEvent.row;
    this.gridStackItem.sizey = gridItemEvent.sizey;
    this.gridStackItem.sizex = gridItemEvent.sizex;
  }

  /**
   * Disable click event if the item have been moved
   * @param event The click event
   */
  protected preventDefault(event: MouseEvent): void {
    if (GridItemUtils.isItemHaveBeenMoved(this.startGridStackItem, this.gridStackItem)) {
      event.preventDefault();
    }
  }

  /**
   * Refresh this project widget
   */
  private refreshProjectWidget(): void {
    this.httpProjectWidgetService.getOneById(this.projectWidget.id).subscribe(projectWidget => {
      this.projectWidget = projectWidget;
      this.appRunScriptDirective.ngOnInit();
    });
  }

  /**
   * Subscribe to widget events
   */
  private initWebsocketConnectionForProjectWidget(): void {
    const projectWidgetSubscriptionUrl = `/user/${this.projectToken}-projectWidget-${this.projectWidget.id}/queue/live`;

    this.websocketService
      .subscribeToDestination(projectWidgetSubscriptionUrl)
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((stompMessage: Stomp.Message) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.WIDGET) {
          this.refreshProjectWidget();
        }
      });
  }

  /**
   * Delete The project widget
   */
  protected displayDeleteProjectWidgetDialog(): void {
    const titlecasePipe = new TitleCasePipe();

    this.dialogService.confirm({
      title: 'widget.delete',
      message: `${this.translateService.instant('delete.confirm')} ${titlecasePipe.transform(this.widget.name)} widget`,
      accept: () => this.httpProjectWidgetService.deleteOneById(this.projectWidget.id).subscribe()
    });
  }

  /**
   * Display the form sidenav used to edit a project widget
   */
  protected displayEditForm(): void {
    this.sidenavService.openFormSidenav({
      title: 'Edit widget',
      formFields: this.projectWidgetFormStepsService.generateProjectWidgetFormFields(this.widget.params),
      save: () => {}
    });
  }

  /**
   * call the popup that display the execution log
   */
  protected displayLogProjectWidgetDialog(): void {
    this.dialogService.info({
      title: 'widget.display.log',
      message: this.projectWidget.log ? this.projectWidget.log : '',
      isErrorMessage: !!this.projectWidget.log
    });
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
  }
}
