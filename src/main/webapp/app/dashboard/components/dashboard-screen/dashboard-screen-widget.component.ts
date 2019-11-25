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
import { MatDialog } from '@angular/material/dialog';
import { NgGridItemConfig, NgGridItemEvent } from 'angular2-grid';
import { takeWhile } from 'rxjs/operators';

import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { Widget } from '../../../shared/models/backend/widget/widget';
import { HttpWidgetService } from '../../../shared/services/backend/http-widget.service';
import { WidgetStateEnum } from '../../../shared/enums/widget-sate.enum';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { HttpProjectWidgetService } from '../../../shared/services/backend/http-project-widget.service';
import { RunScriptsDirective } from '../../../shared/directives/run-scripts.directive';
import { WebsocketService } from '../../../shared/services/frontend/websocket.service';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { GridItemUtils } from '../../../shared/utils/grid-item.utils';
import { CommunicationDialogComponent } from '../../../shared/components/communication-dialog/communication-dialog.component';

import * as Stomp from '@stomp/stompjs';

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
   * Constructor
   *
   * @param matDialog The material dialog
   * @param httpWidgetService The Http widget service
   * @param httpProjectWidgetService The http project widget service
   * @param translateService The translation service
   * @param websocketService The service that manage the websocket
   * @param elementRef Object that get the references of the HTML Elements
   */
  constructor(
    private matDialog: MatDialog,
    private httpWidgetService: HttpWidgetService,
    private httpProjectWidgetService: HttpProjectWidgetService,
    private translateService: TranslateService,
    private websocketService: WebsocketService,
    private elementRef: ElementRef
  ) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.initWebsocketConnectionForProjectWidget();
    setTimeout(() => this.appRunScriptDirective.ngOnInit(), 1000);
    this.startGridStackItem = { ...this.gridStackItem };

    this.httpWidgetService.getById(this.projectWidget.widgetId).subscribe(widget => {
      this.widget = widget;
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
    this.translateService.get(['widget.delete', 'delete.confirm']).subscribe(translations => {
      const titlecasePipe = new TitleCasePipe();

      this.matDialog
        .open(ConfirmDialogComponent, {
          data: {
            title: translations['widget.delete'],
            message: `${translations['delete.confirm']} ${titlecasePipe.transform(this.widget.name)}`
          }
        })
        .afterClosed()
        .subscribe(shouldDeleteProjectWidget => {
          if (shouldDeleteProjectWidget) {
            this.httpProjectWidgetService.deleteOneById(this.projectWidget.id).subscribe();
          }
        });
    });
  }

  /**
   * call the popup that display the execution log
   */
  protected displayLogProjectWidgetDialog(): void {
    this.translateService.get(['widget.display.log']).subscribe(translations => {
      const titlecasePipe = new TitleCasePipe();

      this.matDialog.open(CommunicationDialogComponent, {
        minWidth: 700,
        height: '80%',
        data: {
          title: titlecasePipe.transform(translations['widget.display.log']),
          message: this.projectWidget.log ? this.projectWidget.log : '',
          isErrorMessage: !!this.projectWidget.log
        }
      });
    });
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
  }
}
