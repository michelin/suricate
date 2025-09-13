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
import { Component, inject, OnInit } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonColorEnum } from '../../enums/button-color.enum';
import { IconEnum } from '../../enums/icon.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { ActionsDialogConfiguration } from '../../models/frontend/dialog/actions-dialog-configuration';
import { Buttons } from '../buttons/buttons';

@Component({
	templateUrl: './actions-dialog.html',
	styleUrls: ['./actions-dialog.scss'],
	imports: [MatDialogTitle, CdkScrollable, MatDialogContent, MatDialogActions, Buttons, MatDialogClose, TranslatePipe]
})
export class ActionsDialog implements OnInit {
	private readonly data = inject<ActionsDialogConfiguration>(MAT_DIALOG_DATA);

	/**
	 * The configuration of the confirmation dialog
	 */
	public configuration: ActionsDialogConfiguration;

	/**
	 * Init method
	 */
	ngOnInit(): void {
		this.configuration = this.data;
		this.initCloseButtonConfiguration();
	}

	/**
	 * Init the buttons configurations
	 */
	private initCloseButtonConfiguration(): void {
		const closeButton: ButtonConfiguration<void> = {
			label: 'close',
			icon: IconEnum.CLOSE,
			color: ButtonColorEnum.WARN
		};

		this.configuration.actions = [closeButton].concat(this.configuration.actions);
	}
}
