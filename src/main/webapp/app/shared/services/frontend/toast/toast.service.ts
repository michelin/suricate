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

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

import { ToastType } from '../../../enums/toast-type';
import { ToastMessage } from '../../../models/frontend/toast/toast-message';

/**
 * The service that manage toast notification message
 */
@Injectable({ providedIn: 'root' })
export class ToastService {
	/**
	 * The toast message subject
	 */
	private toastMessageSubject = new BehaviorSubject<ToastMessage>(null);

	/**
	 * Get the toast message events
	 * @returns {Observable<ToastMessage>}
	 */
	public listenForToastMessages(): Observable<ToastMessage> {
		return this.toastMessageSubject.asObservable();
	}

	/**
	 * A new message to toast component
	 * @param title The title of the message
	 * @param style The message style
	 * @param content The content of the message
	 */
	public sendMessage(title: string, style?: ToastType, content?: string): void {
		this.toastMessageSubject.next(new ToastMessage(title, content, style));
	}
}
