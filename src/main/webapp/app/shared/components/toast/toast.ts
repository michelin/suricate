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

import { animate, group, state, style, transition, trigger } from '@angular/animations';
import { NgClass } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Icon } from '../../enums/icon';
import { ToastType } from '../../enums/toast-type';
import { MaterialIconRecords } from '../../models/frontend/icon/material-icon';
import { ToastMessage } from '../../models/frontend/toast/toast-message';
import { ToastService } from '../../services/frontend/toast/toast-service';

/**
 * Component that display toast notification messages
 */
@Component({
	selector: 'suricate-toast-messages',
	templateUrl: './toast.component.html',
	styleUrls: ['./toast.component.scss'],
	animations: [
		trigger('slideInOut', [
			state(
				'in',
				style({
					'max-height': '500px',
					'opacity': '1',
					'visibility': 'visible'
				})
			),
			state(
				'out',
				style({
					'max-height': '0px',
					'opacity': '0',
					'visibility': 'hidden'
				})
			),
			transition('in => out', [
				group([
					animate(
						'400ms ease-in-out',
						style({
							opacity: '0'
						})
					),
					animate(
						'600ms ease-in-out',
						style({
							'max-height': '0px'
						})
					),
					animate(
						'700ms ease-in-out',
						style({
							visibility: 'hidden'
						})
					)
				])
			]),
			transition('out => in', [
				group([
					animate(
						'1ms ease-in-out',
						style({
							visibility: 'visible'
						})
					),
					animate(
						'600ms ease-in-out',
						style({
							'max-height': '500px'
						})
					),
					animate(
						'800ms ease-in-out',
						style({
							opacity: '1'
						})
					)
				])
			])
		])
	],
	imports: [NgClass, MatIcon, MatIconButton, TranslatePipe]
})
export class ToastComponent implements OnInit, OnDestroy {
	private readonly toastService = inject(ToastService);

	/**
	 * Subject used to unsubscribe all the subscriptions when the component is destroyed
	 */
	private readonly unsubscribe: Subject<void> = new Subject<void>();

	/**
	 * The component state
	 */
	public animationState = 'out';

	/**
	 * The enums of toast type
	 */
	public toastType = ToastType;

	/**
	 * The message to display
	 */
	public message: ToastMessage;

	/**
	 * The current timer timeout
	 */
	private timeout: NodeJS.Timeout;

	/**
	 * The list of icons
	 */
	public iconEnum = Icon;

	/**
	 * The list of material icon codes
	 */
	public materialIconRecords = MaterialIconRecords;

	/**
	 * Called when the component is init
	 */
	public ngOnInit(): void {
		this.toastService
			.listenForToastMessages()
			.pipe(takeUntil(this.unsubscribe))
			.subscribe((message: ToastMessage) => {
				this.message = message;
				if (message) {
					this.showToast();
				}
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
	 * Show the toast notification
	 */
	private showToast(): void {
		this.clearTimeout();
		this.animationState = 'in';
		this.hideWithinTimeout();
	}

	/**
	 * Hide manually the toast notification
	 */
	public hideToast(): void {
		this.clearTimeout();
		this.animationState = 'out';
	}

	/**
	 * Hide the toast notification with timer
	 */
	private hideWithinTimeout(): void {
		this.timeout = setTimeout(() => this.hideToast(), 4000);
	}

	/**
	 * Clear the timer
	 */
	private clearTimeout(): void {
		clearTimeout(this.timeout);
	}
}
