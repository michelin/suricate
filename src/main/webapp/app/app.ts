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

import { OverlayContainer } from '@angular/cdk/overlay';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { SettingsService } from './core/services/settings.service';
import { SidenavComponent } from './layout/components/sidenav/sidenav.component';
import { ActionsDialogComponent } from './shared/components/actions-dialog/actions-dialog.component';
import { CommunicationDialogComponent } from './shared/components/communication-dialog/communication-dialog.component';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { ToastComponent } from './shared/components/toast/toast.component';
import { ActionsDialogConfiguration } from './shared/models/frontend/dialog/actions-dialog-configuration';
import { CommunicationDialogConfiguration } from './shared/models/frontend/dialog/communication-dialog-configuration';
import { ConfirmationDialogConfiguration } from './shared/models/frontend/dialog/confirmation-dialog-configuration';
import { DialogService } from './shared/services/frontend/dialog/dialog.service';

@Component({
	selector: 'suricate-root',
	templateUrl: './app.html',
	styleUrls: ['./app.scss'],
	imports: [SidenavComponent, ToastComponent]
})
export class App implements OnInit, OnDestroy {
	private readonly matDialog = inject(MatDialog);
	private readonly overlayContainer = inject(OverlayContainer);
	private readonly settingsService = inject(SettingsService);
	private readonly dialogService = inject(DialogService);

	/**
	 * The current theme
	 */
	public theme: string;

	/**
	 * Subject used to unsubscribe all the subscriptions when the component is destroyed
	 */
	private readonly unsubscribe: Subject<void> = new Subject<void>();

	/**
	 * Called at the init of the app
	 */
	public ngOnInit(): void {
		this.subscribeToConfirmationDialog();
		this.subscribeToCommunicationDialog();
		this.subscribeToActionsDialog();
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
				this.overlayContainer.getContainerElement().parentElement.classList.remove(this.theme);
				this.overlayContainer.getContainerElement().parentElement.classList.add(theme);
				this.theme = theme;
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
					width: '600px',
					height: '200px',
					data: confirmationConfiguration,
					autoFocus: false
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
					data: communicationDialogConfiguration,
					autoFocus: false
				};

				this.matDialog.open(CommunicationDialogComponent, dialogConfig);
			});
	}

	/**
	 * Function that display the actions' dialog when using the dialog service
	 */
	private subscribeToActionsDialog(): void {
		this.dialogService
			.listenActionsMessages()
			.pipe(takeUntil(this.unsubscribe))
			.subscribe((actionsDialogConfiguration: ActionsDialogConfiguration) => {
				const dialogConfig: MatDialogConfig = {
					role: 'dialog',
					width: '600px',
					height: '200px',
					data: actionsDialogConfiguration
				};

				this.matDialog.open(ActionsDialogComponent, dialogConfig);
			});
	}
}
