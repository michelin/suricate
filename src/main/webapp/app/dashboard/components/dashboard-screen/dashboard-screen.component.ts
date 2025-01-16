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

import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  Renderer2,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { KtdGridComponent, KtdGridItemComponent, KtdGridItemPlaceholder } from '@katoid/angular-grid-layout';
import { KtdGridLayout } from '@katoid/angular-grid-layout/lib/grid.definitions';
import { IMessage } from '@stomp/rx-stomp';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { IconEnum } from '../../../shared/enums/icon.enum';
import { WebsocketUpdateTypeEnum } from '../../../shared/enums/websocket-update-type.enum';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectWidget } from '../../../shared/models/backend/project-widget/project-widget';
import { ProjectWidgetPositionRequest } from '../../../shared/models/backend/project-widget/project-widget-position-request';
import { GridOptions } from '../../../shared/models/frontend/grid/grid-options';
import { WebsocketUpdateEvent } from '../../../shared/models/frontend/websocket/websocket-update-event';
import { SafeHtmlPipe } from '../../../shared/pipes/safe-html/safe-html.pipe';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { WebsocketService } from '../../../shared/services/frontend/websocket/websocket.service';
import { GridItemUtils } from '../../../shared/utils/grid-item.utils';
import { LibraryService } from '../../services/library/library.service';
import { DashboardScreenWidgetComponent } from './dashboard-screen-widget/dashboard-screen-widget.component';

declare global {
  interface Window {
    page_loaded: boolean;
  }
}

/**
 * Display the grid stack widgets
 */
@Component({
  selector: 'suricate-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  styleUrls: ['./dashboard-screen.component.scss'],
  standalone: true,
  imports: [
    MatIcon,
    KtdGridComponent,
    KtdGridItemComponent,
    DashboardScreenWidgetComponent,
    KtdGridItemPlaceholder,
    SafeHtmlPipe
  ]
})
export class DashboardScreenComponent implements AfterViewInit, OnChanges, OnDestroy {
  /**
   * Reference on the span containing all the required JS libraries
   */
  @ViewChild('externalJsLibraries')
  public externalJsLibrariesSpan: ElementRef<HTMLSpanElement>;

  /**
   * The project to display
   */
  @Input()
  public project: Project;

  /**
   * The project widget list
   */
  @Input()
  public projectWidgets: ProjectWidget[];

  /**
   * Tell if the dashboard should be on readOnly or not
   */
  @Input()
  public readOnly = true;

  /**
   * The screen code
   */
  @Input()
  public screenCode: number;

  /**
   * Tell if the websockets need to be opened.
   * E.g.: In case of grids rotation, do not open websockets for each grid
   */
  @Input()
  public openWebsockets = true;

  /**
   * Event for handling the disconnection
   */
  @Output()
  public disconnectEvent = new EventEmitter<void>();

  /**
   * Use to tell to the parent component that it should refresh the project widgets
   */
  @Output()
  public refreshAllProjectWidgets = new EventEmitter<void>();

  /**
   * Subject used to unsubscribe all the subscriptions to project web sockets
   */
  private unsubscribeProjectWebSocket: Subject<void> = new Subject<void>();

  /**
   * The grid options
   */
  public gridOptions: GridOptions;

  /**
   * All the grids of the dashboard
   */
  public currentGrid: KtdGridLayout = [];

  /**
   * Tell if we should display the screen code
   */
  public shouldDisplayScreenCode = false;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The constructor
   *
   * @param renderer The renderer Angular entity
   * @param httpProjectService Back-End service used to manage the project
   * @param websocketService Front-End service used to manage the web sockets
   * @param libraryService Front-End service used to manage the libraries
   */
  constructor(
    private readonly renderer: Renderer2,
    private readonly httpProjectService: HttpProjectService,
    private readonly websocketService: WebsocketService,
    private readonly libraryService: LibraryService
  ) {}

  /**
   * On changes method
   *
   * @param changes The change event
   */
  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['project']) {
      if (!changes['project'].previousValue) {
        // Inject this variable in the window scope because some widgets use it to init the js
        window.page_loaded = true;
      }

      if (changes['project'].currentValue) {
        this.initGridStackOptions();

        // Do not add libs in the DOM at first view init
        // Let the after view init method handle the first initialization
        if (!changes['project'].firstChange) {
          this.addExternalJSLibrariesToTheDOM();
        }

        if (!changes['project'].previousValue) {
          this.initProjectWebsockets();
        } else if (changes['project'].previousValue.token !== changes['project'].currentValue.token) {
          this.resetProjectWebsockets();
        }
      }
    }

    if (changes['readOnly']) {
      this.initGridStackOptions();
    }

    if (changes['projectWidgets']) {
      this.initGrid();
    }
  }

  /**
   * After view init method
   */
  public ngAfterViewInit(): void {
    this.addExternalJSLibrariesToTheDOM();
  }

  /**
   * When the component is destroyed (new page)
   */
  public ngOnDestroy(): void {
    this.disconnectFromWebsockets();
  }

  /**
   * For each JS libraries linked with the project, create a script element with the URL of the library
   * and a callback which notify subscribers when the library is loaded.
   */
  public addExternalJSLibrariesToTheDOM() {
    if (this.project) {
      this.libraryService.init(this.project.librariesToken.length);

      if (this.project.librariesToken.length > 0) {
        this.project.librariesToken.forEach((token) => {
          const script: HTMLScriptElement = document.createElement('script');
          script.type = 'text/javascript';
          script.src = HttpAssetService.getContentUrl(token);
          script.onload = () => this.libraryService.markScriptAsLoaded(token);
          script.async = false;

          this.renderer.appendChild(this.externalJsLibrariesSpan.nativeElement, script);
        });
      }

      // No library to load
      else {
        this.libraryService.emitAreJSScriptsLoaded(true);
      }
    }
  }

  /**
   * Display the screen code
   */
  private displayScreenCode(): void {
    this.shouldDisplayScreenCode = true;
    setTimeout(() => (this.shouldDisplayScreenCode = false), 10000);
  }

  /**********************************************************************************************************/
  /*                                         GRID MANAGEMENT                                                */
  /**********************************************************************************************************/

  /**
   * Init the options for Grid Stack plugin
   */
  private initGridStackOptions(): void {
    if (this.project) {
      this.gridOptions = {
        cols: this.project.gridProperties.maxColumn,
        rowHeight: this.project.gridProperties.widgetHeight,
        gap: 5,
        draggable: false,
        resizable: false,
        compactType: undefined
      };

      if (!this.readOnly) {
        this.gridOptions = {
          ...this.gridOptions,
          draggable: true,
          resizable: true
        };
      }
    }
  }

  /**
   * Create the list of grid items used to display widgets on the grids
   */
  private initGrid(): void {
    if (this.projectWidgets) {
      this.currentGrid = this.getGridLayoutFromProjectWidgets();
    }
  }

  /**
   * Get the list of GridItemConfigs from project widget
   */
  private getGridLayoutFromProjectWidgets(): KtdGridLayout {
    const layout: KtdGridLayout = [];

    this.projectWidgets.forEach((projectWidget: ProjectWidget) => {
      layout.push({
        id: String(projectWidget.id),
        x: projectWidget.widgetPosition.gridColumn - 1,
        y: projectWidget.widgetPosition.gridRow - 1,
        w: projectWidget.widgetPosition.width,
        h: projectWidget.widgetPosition.height
      });
    });

    return layout;
  }

  /**********************************************************************************************************/
  /*                                      WEBSOCKET MANAGEMENT                                              */
  /**********************************************************************************************************/

  /**
   * Init web sockets for project events
   */
  private initProjectWebsockets(): void {
    if (this.openWebsockets) {
      this.unsubscribeProjectWebSocket = new Subject<void>();
      this.websocketProjectEventSubscription();
      this.websocketProjectScreenEventSubscription();
    }
  }

  /**
   * Reset the project web sockets subscriptions
   */
  private resetProjectWebsockets(): void {
    if (this.openWebsockets) {
      this.unsubscribeProjectWebsockets();
      this.initProjectWebsockets();
    }
  }

  /**
   * Disconnect from web sockets
   */
  private disconnectFromWebsockets(): void {
    this.unsubscribeProjectWebsockets();
  }

  /**
   * Unsubscribe to every current project websockets connections
   */
  private unsubscribeProjectWebsockets(): void {
    this.unsubscribeProjectWebSocket.next();
    this.unsubscribeProjectWebSocket.complete();
  }

  /**
   * Create a websocket subscription for the current project
   */
  private websocketProjectEventSubscription(): void {
    const projectSubscriptionUrl = `/user/${this.project.token}/queue/live`;

    this.websocketService
      .watch(projectSubscriptionUrl)
      .pipe(takeUntil(this.unsubscribeProjectWebSocket))
      .subscribe((stompMessage: IMessage) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        switch (updateEvent.type) {
          case WebsocketUpdateTypeEnum.DISCONNECT:
            this.disconnectFromWebsockets();
            this.disconnectEvent.emit();
            break;
          case WebsocketUpdateTypeEnum.DISPLAY_NUMBER:
            this.displayScreenCode();
            break;
          case WebsocketUpdateTypeEnum.REFRESH_DASHBOARD:
            this.refreshAllProjectWidgets.emit();
            break;
          case WebsocketUpdateTypeEnum.RELOAD:
            location.reload();
            break;
          default:
            this.refreshAllProjectWidgets.emit();
        }
      });
  }

  /**
   * Create a websocket subscription for the current screen
   */
  private websocketProjectScreenEventSubscription(): void {
    const screenSubscriptionUrl = `/user/${this.project.token}-${this.screenCode}/queue/unique`;

    this.websocketService
      .watch(screenSubscriptionUrl)
      .pipe(takeUntil(this.unsubscribeProjectWebSocket))
      .subscribe((stompMessage: IMessage) => {
        const updateEvent: WebsocketUpdateEvent = JSON.parse(stompMessage.body);

        if (updateEvent.type === WebsocketUpdateTypeEnum.DISCONNECT) {
          this.disconnectFromWebsockets();
          this.disconnectEvent.emit();
        }
      });
  }

  /**
   * When the layout is updated
   * @param layout The new layout
   */
  public onLayoutUpdated(layout: KtdGridLayout) {
    if (this.isGridItemsHasMoved(layout)) {
      this.currentGrid = layout;

      const projectWidgetPositionRequests: ProjectWidgetPositionRequest[] = [];
      this.currentGrid.forEach((gridItem) => {
        projectWidgetPositionRequests.push({
          projectWidgetId: Number(gridItem.id),
          gridColumn: gridItem.x + 1,
          gridRow: gridItem.y + 1,
          height: gridItem.h,
          width: gridItem.w
        });
      });

      this.httpProjectService
        .updateProjectWidgetPositions(this.project.token, projectWidgetPositionRequests)
        .subscribe();
    }
  }

  /**
   * Tell if the grid items have moved
   * @param layout The new layout
   * @private
   */
  private isGridItemsHasMoved(layout: KtdGridLayout): boolean {
    let itemHaveBeenMoved = false;

    this.currentGrid.forEach((currentGridItem) => {
      const gridItemFound = layout.find((newGridItem) => {
        return currentGridItem.id === newGridItem.id;
      });

      if (gridItemFound && GridItemUtils.isItemHaveBeenMoved(currentGridItem, gridItemFound)) {
        itemHaveBeenMoved = true;
      }
    });

    return itemHaveBeenMoved;
  }

  /**
   * Get the project widget by its id
   * @param id The id of the project widget
   */
  public getProjectWidgetById(id: string): ProjectWidget {
    return this.projectWidgets.find((projectWidget) => projectWidget.id === Number(id));
  }
}
