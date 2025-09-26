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

import { Directive, effect, ElementRef, inject, input } from '@angular/core';

import { ProjectWidget } from '../../models/backend/project-widget/project-widget';

/**
 * Directive for Widget's JS scripts
 */
@Directive({
	selector: '[widgetHtmlDirective]'
})
export class WidgetHtmlDirective {
	private readonly elementRef = inject(ElementRef);

	/**
	 * The rendered project widget
	 */
	public projectWidget = input<ProjectWidget>();

	/**
	 * Constructor.
	 */
	constructor() {
		effect(() => {
			this.projectWidget();
			this.reapplyJSScripts();
		});
	}

	/**
	 * From all the JS scripts contained by the current widget HTML section, build new scripts then insert them in the DOM.
	 * It executes the scripts again and render the widget properly.
	 * This is called once the HTML of the widget is fully loaded.
	 */
	private reapplyJSScripts() {
		const scripts: HTMLScriptElement[] = (
			Array.from(this.elementRef.nativeElement.getElementsByTagName('script')) as HTMLScriptElement[]
		).filter((currentScript) => currentScript.innerHTML);

		Array.from(Array(scripts.length).keys()).forEach((index: number) => {
			const script = scripts[index];

			const copyScript: HTMLScriptElement = document.createElement('script');
			copyScript.type = script.type ? script.type : 'text/javascript';
			copyScript.innerHTML = script.innerHTML;
			copyScript.async = false;
			script.parentNode.replaceChild(copyScript, script);
		});
	}
}
