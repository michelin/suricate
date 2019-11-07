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
import { takeWhile } from 'rxjs/operators';

import { DialogService } from './shared/services/frontend/dialog.service';
import { MatDialog } from '@angular/material';
import { ConfirmationDialogConfiguration } from './shared/models/frontend/dialog/confirmation-dialog-configuration';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { MatDialogConfig } from '@angular/material/typings/dialog';
import { SettingsService } from './core/services/settings.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'suricate-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  /**
   * The HTML class attribute
   */
  @HostBinding('class') appHtmlClass;
  /**
   * Tell if the component is instantiate or not
   *
   * @type {boolean}
   * @private
   */
  private isAlive = true;
  /**
   * If we should hide the menu
   */
  public shouldHideMenu = true;

  /**
   * The constructor
   *
   * @param settingsService
   * @param {OverlayContainer} overlayContainer The overlay container service
   * @param {DialogService} dialogService Angular service used to manage dialogs
   * @param {MatDialog} matDialog Angular material service used to display dialog
   * @param router Angular service used to manage routes
   * @param activatedRoute Angular service used to get the activated route by the component
   */
  constructor(
    private settingsService: SettingsService,
    private overlayContainer: OverlayContainer,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) {}

  /**
   * Called at the init of the app
   */
  ngOnInit() {
    this.settingsService.currentTheme$.pipe(takeWhile(() => this.isAlive)).subscribe(themeValue => {
      this.switchTheme(themeValue);
    });

    this.settingsService.initDefaultSettings();
    this.subscribeToConfirmationDialog();
  }

  /**
   * Function that display the confirmation dialog when using the dialog service
   */
  private subscribeToConfirmationDialog(): void {
    this.dialogService
      .listenConfirmationMessages()
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((confirmationConfiguration: ConfirmationDialogConfiguration) => {
        const dialogConfig: MatDialogConfig = {
          role: 'dialog',
          width: '500px',
          data: confirmationConfiguration
        };

        this.matDialog.open(ConfirmDialogComponent, dialogConfig);
      });
  }

  /**
   * Switch the theme
   * @param {string} themeValue The new theme value
   */
  switchTheme(themeValue: string) {
    this.overlayContainer.getContainerElement().classList.remove(this.appHtmlClass);
    this.overlayContainer.getContainerElement().classList.add(themeValue);
    this.appHtmlClass = themeValue;
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this.isAlive = false;
  }
}
