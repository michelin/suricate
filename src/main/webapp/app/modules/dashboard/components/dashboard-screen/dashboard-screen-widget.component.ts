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


import {Component, ElementRef, HostBinding, Input, OnDestroy, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {TitleCasePipe} from '@angular/common';
import {MatDialog} from '@angular/material';
import {NgGridItemConfig, NgGridItemEvent} from 'angular2-grid';
import {takeWhile} from 'rxjs/operators';

import {ProjectWidget} from '../../../../shared/model/api/ProjectWidget/ProjectWidget';
import {Widget} from '../../../../shared/model/api/widget/Widget';
import {HttpWidgetService} from '../../../../shared/services/api/http-widget.service';
import {WidgetStateEnum} from '../../../../shared/model/enums/WidgetSateEnum';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {HttpProjectWidgetService} from '../../../../shared/services/api/http-project-widget.service';
import {EditProjectWidgetDialogComponent} from '../edit-project-widget-dialog/edit-project-widget-dialog.component';
import {RunScriptsDirective} from '../../../../shared/directives/run-scripts.directive';
import {WebsocketService} from '../../../../shared/services/websocket.service';
import {WSUpdateEvent} from '../../../../shared/model/websocket/WSUpdateEvent';
import {WSUpdateType} from '../../../../shared/model/websocket/enums/WSUpdateType';
import {GridItemUtils} from '../../../../shared/utils/GridItemUtils';

import * as Stomp from '@stomp/stompjs';
import {CommunicationDialogComponent} from '../../../../shared/components/communication-dialog/communication-dialog.component';

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'app-dashboard-screen-widget',
  templateUrl: './dashboard-screen-widget.component.html',
  styleUrls: ['./dashboard-screen-widget.component.scss']
})
export class DashboardScreenWidgetComponent implements OnInit, OnDestroy {

  /**
   * The projectWidget to display
   * @type {ProjectWidget}
   */
  @Input() projectWidget: ProjectWidget;
  /**
   * The grid item config
   * @type {NgGridItemConfig}
   */
  @Input() gridStackItem: NgGridItemConfig;
  /**
   * Tell if we are on
   * @type {boolean}
   */
  @Input() readOnly: boolean;
  /**
   * The project token
   * @type {string}
   */
  @Input() projectToken: string;

  /**
   * Add runScriptsDirective, so we can recall it
   */
  @HostBinding('attr.appRunScripts') appRunScriptDirective = new RunScriptsDirective(this.elementRef);

  /**
   * The widget related to this project widget
   */
  widget: Widget;

  /**
   * The enumeration that hold the state of a widget (used in HTML)
   */
  WidgetStateEnum = WidgetStateEnum;

  /**
   * True when the component is alive
   */
  isAlive = true;
  /**
   * Init element info
   */
  startGridStackItem: NgGridItemConfig;

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
  constructor(private matDialog: MatDialog,
              private httpWidgetService: HttpWidgetService,
              private httpProjectWidgetService: HttpProjectWidgetService,
              private translateService: TranslateService,
              private websocketService: WebsocketService,
              private elementRef: ElementRef) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit(): void {
    this.initWebsocketConnectionForProjectWidget();
    setTimeout(() => this.appRunScriptDirective.ngOnInit(), 1000);
    this.startGridStackItem = {...this.gridStackItem};

    this.httpWidgetService.getOneById(this.projectWidget.widgetId).subscribe(widget => {
      this.widget = widget;
    });
  }

  /**
   * Register the new posisition of the element
   *
   * @param gridItemEvent The grid item event
   */
  registerNewPosition(gridItemEvent: NgGridItemEvent) {
    this.gridStackItem.col = gridItemEvent.col;
    this.gridStackItem.row = gridItemEvent.row;
    this.gridStackItem.sizey = gridItemEvent.sizey;
    this.gridStackItem.sizex = gridItemEvent.sizex;
  }

  /**
   * Disable click event if the item have been moved
   * @param event The click event
   */
  preventDefault(event: MouseEvent) {
    if (GridItemUtils.isItemHaveBeenMoved(this.startGridStackItem, this.gridStackItem)) {
      event.preventDefault();
    }
  }

  /**
   * Refresh this project widget
   */
  refreshProjectWidget() {
    this.httpProjectWidgetService.getOneById(this.projectWidget.id).subscribe(projectWidget => {
      this.projectWidget = projectWidget;
      this.appRunScriptDirective.ngOnInit();
    });
  }

  /**
   * Subscribe to widget events
   */
  initWebsocketConnectionForProjectWidget() {
    const projectWidgetSubscriptionUrl = `/user/${this.projectToken}-projectWidget-${this.projectWidget.id}/queue/live`;

    this.websocketService.subscribeToDestination(projectWidgetSubscriptionUrl).pipe(
      takeWhile(() => this.isAlive)
    ).subscribe((stompMessage: Stomp.Message) => {
      const updateEvent: WSUpdateEvent = JSON.parse(stompMessage.body);

      if (updateEvent.type === WSUpdateType.WIDGET) {
        this.refreshProjectWidget();
      }
    });
  }

  /**
   * Delete The project widget
   */
  displayDeleteProjectWidgetDialog(): void {
    this.translateService.get(['widget.delete', 'delete.confirm']).subscribe(translations => {
      const titlecasePipe = new TitleCasePipe();

      this.matDialog.open(ConfirmDialogComponent, {
        data: {
          title: translations['widget.delete'],
          message: `${translations['delete.confirm']} ${titlecasePipe.transform(this.widget.name)}`
        }
      }).afterClosed().subscribe(shouldDeleteProjectWidget => {
        if (shouldDeleteProjectWidget) {
          this.httpProjectWidgetService.deleteOneById(this.projectWidget.id).subscribe();
        }
      });

    });
  }

  /**
   * Display the dialog to edit a project widget
   */
  displayEditProjectWidgetDialog(): void {
    this.matDialog.open(EditProjectWidgetDialogComponent, {
      minWidth: 700,
      data: {projectWidgetId: this.projectWidget.id}
    });
  }

  /**
   * call the popup that display the execution log
   */
  displayLogProjectWidgetDialog(): void {
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
  ngOnDestroy(): void {
    this.isAlive = false;
  }
}
