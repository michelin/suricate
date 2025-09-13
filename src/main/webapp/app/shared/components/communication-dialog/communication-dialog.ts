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

import { CdkScrollable } from '@angular/cdk/scrolling';
import { NgClass } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonColor } from '../../enums/button-color';
import { Icon } from '../../enums/icon';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { CommunicationDialogConfiguration } from '../../models/frontend/dialog/communication-dialog-configuration';
import { Buttons } from '../buttons/buttons';

/**
 * This component is used to display information (without actions to do)
 */
@Component({
	selector: 'suricate-communication-dialog',
	templateUrl: './communication-dialog.html',
	styleUrls: ['./communication-dialog.scss'],
	imports: [
		MatDialogTitle,
		CdkScrollable,
		MatDialogContent,
		NgClass,
		MatDialogActions,
		Buttons,
		MatDialogClose,
		TranslatePipe
	]
})
export class CommunicationDialog implements OnInit {
	private readonly data = inject<CommunicationDialogConfiguration>(MAT_DIALOG_DATA);

	/**
	 * The configuration of the confirmation dialog
	 */
	public configuration: CommunicationDialogConfiguration;

	/**
	 * The buttons
	 */
	public buttons: ButtonConfiguration<unknown>[] = [];

	/**
	 * Called when the dialog is init
	 */
	public ngOnInit(): void {
		this.initButtons();
		this.configuration = this.data;
	}

	/**
	 * Init the buttons
	 */
	private initButtons(): void {
		this.buttons.push({
			label: 'close',
			icon: Icon.CLOSE,
			color: ButtonColor.WARN
		});
	}
}
