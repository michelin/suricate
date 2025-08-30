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

import { Component, inject, Input, OnInit } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { ButtonsComponent } from '../../../shared/components/buttons/buttons.component';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';

/**
 * The page header component
 */
@Component({
	selector: 'suricate-pages-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.scss'],
	imports: [ButtonsComponent, MatDivider, TranslatePipe]
})
export class HeaderComponent implements OnInit {
	private readonly route = inject(Router);

	/**
	 * The configuration of the header
	 */
	@Input()
	public configuration: HeaderConfiguration;

	/**
	 * True if the menu should be display on the page
	 */
	@Input()
	public showMenu = true;

	/**
	 * The page name
	 */
	public pageName: string;

	/**
	 * When the component is init
	 */
	ngOnInit() {
		this.pageName = this.route.url.split('/')[1];
	}
}
