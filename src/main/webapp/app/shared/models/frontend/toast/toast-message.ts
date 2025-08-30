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

import { ToastTypeEnum } from '../../../enums/toast-type.enum';

/**
 * Class that represent a toaster notification message
 */
export class ToastMessage {
	/**
	 * The title notification
	 */
	title: string;
	/**
	 * The string content
	 */
	content: string;
	/**
	 * The style to apply (success, info, ...)
	 */
	style: ToastTypeEnum;
	/**
	 * Triggered by the user to hide the message
	 *
	 * @type {boolean}
	 */
	dismissed = false;

	/**
	 * Class constructor
	 *
	 * @param title The message title
	 * @param content The message content
	 * @param style The style
	 */
	constructor(title: string, content?: string, style?: ToastTypeEnum) {
		this.title = title;
		this.content = content || '';
		this.style = style || ToastTypeEnum.INFO;
	}
}
