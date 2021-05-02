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

import { Component, HostBinding, OnDestroy, OnInit } from '@angular/core';
import { OverlayContainer } from '@angular/cdk/overlay';
import { takeUntil, takeWhile } from 'rxjs/operators';

import { DialogService } from './shared/services/frontend/dialog/dialog.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ConfirmationDialogConfiguration } from './shared/models/frontend/dialog/confirmation-dialog-configuration';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { SettingsService } from './core/services/settings.service';
import { CommunicationDialogConfiguration } from './shared/models/frontend/dialog/communication-dialog-configuration';
import { CommunicationDialogComponent } from './shared/components/communication-dialog/communication-dialog.component';
import { AuthenticationService } from './shared/services/frontend/authentication/authentication.service';
import { from, Subject } from 'rxjs';
import {UserSetting} from "./shared/models/backend/setting/user-setting";

/**
 * Main component init the application
 */
@Component({
  selector: 'suricate-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  /**
   * The HTML class attribute
   */
  @HostBinding('class')
  public appHtmlClass: string;

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * The constructor
   *
   * @param matDialog Angular material service used to display dialog
   * @param overlayContainer Angular service used to manage DOM information
   * @param settingsService Suricate service used to manage the settings
   * @param dialogService Frontend service used to manage dialogs
   */
  constructor(
    private readonly matDialog: MatDialog,
    private readonly overlayContainer: OverlayContainer,
    private readonly settingsService: SettingsService,
    private readonly dialogService: DialogService
  ) {}

  /**
   * Called at the init of the app
   */
  public ngOnInit(): void {
    this.subscribeToConfirmationDialog();
    this.subscribeToCommunicationDialog();
    this.subscribeToThemeChanging();

    this.settingsService.initDefaultSettings();
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Used to change the current when asked
   */
  private subscribeToThemeChanging(): void {
    this.settingsService
      .getCurrentThemeValue()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((theme: string) => {
        this.overlayContainer.getContainerElement().classList.remove(this.appHtmlClass);
        this.overlayContainer.getContainerElement().classList.add(theme);
        this.appHtmlClass = theme;
      });
  }

  /**
   * Function that display the confirmation dialog when using the dialog service
   */
  private subscribeToConfirmationDialog(): void {
    this.dialogService
      .listenConfirmationMessages()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((confirmationConfiguration: ConfirmationDialogConfiguration) => {
        const dialogConfig: MatDialogConfig = {
          role: 'dialog',
          width: '700px',
          height: '50%',
          data: confirmationConfiguration
        };

        this.matDialog.open(ConfirmDialogComponent, dialogConfig);
      });
  }

  /**
   * Function that display the communication dialog when using the dialog service
   */
  private subscribeToCommunicationDialog(): void {
    this.dialogService
      .listenCommunicationMessages()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((communicationDialogConfiguration: CommunicationDialogConfiguration) => {
        const dialogConfig: MatDialogConfig = {
          role: 'dialog',
          width: '700px',
          height: '80%',
          data: communicationDialogConfiguration
        };

        this.matDialog.open(CommunicationDialogComponent, dialogConfig);
      });
  }
}
