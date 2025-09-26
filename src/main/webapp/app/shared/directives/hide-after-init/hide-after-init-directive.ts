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

import { AfterViewInit, Directive, effect, ElementRef, inject, input } from '@angular/core';

@Directive({
	selector: '[hideAfterInit]'
})
export class HideAfterInitDirective implements AfterViewInit {
	private readonly elementRef = inject(ElementRef);

	/**
	 * True if the element should be hidden
	 */
	public hide = input<boolean>();

	/**
	 * True if the directive has been initialized
	 */
	private hasInitialized = false;

	/**
	 * Constructor.
	 */
	constructor() {
		effect(() => {
			const hideValue = this.hide();

			if (this.hasInitialized) {
				this.elementRef.nativeElement.style.display = hideValue ? 'none' : 'block';
			}
		});
	}

	/**
	 * After view init
	 */
	ngAfterViewInit(): void {
		// Let the time to Js to render the element before hiding it
		setTimeout(() => {
			this.elementRef.nativeElement.style.display = this.hide() ? 'none' : 'block';
			this.hasInitialized = true;
		}, 100);
	}
}
