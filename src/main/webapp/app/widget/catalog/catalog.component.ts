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

import { CdkDrag, CdkDropList } from '@angular/cdk/drag-drop';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Header } from '../../layout/components/header/header';
import { Buttons } from '../../shared/components/buttons/buttons';
import { Input } from '../../shared/components/inputs/input/input';
import { List } from '../../shared/components/list/list';
import { Paginator } from '../../shared/components/paginator/paginator';
import { Spinner } from '../../shared/components/spinner/spinner';
import { Widget } from '../../shared/models/backend/widget/widget';
import { WidgetRequest } from '../../shared/models/backend/widget/widget-request';
import { AbstractHttpService } from '../../shared/services/backend/abstract-http/abstract-http.service';
import { HttpAssetService } from '../../shared/services/backend/http-asset/http-asset.service';
import { HttpWidgetService } from '../../shared/services/backend/http-widget/http-widget.service';

/**
 * Component used to display the list of widgets
 */
@Component({
	templateUrl: '../../shared/components/list/list.html',
	styleUrls: ['../../shared/components/list/list.scss'],
	imports: [
		Header,
		Input,
		FormsModule,
		ReactiveFormsModule,
		Spinner,
		CdkDropList,
		CdkDrag,
		NgClass,
		NgOptimizedImage,
		Buttons,
		Paginator
	],
	providers: [{ provide: AbstractHttpService, useClass: HttpWidgetService }]
})
export class CatalogComponent extends List<Widget, WidgetRequest> {
	/**
	 * Constructor
	 */
	constructor() {
		super();

		this.initHeaderConfiguration();
		this.initFilter();
	}

	/**
	 * {@inheritDoc}
	 */
	public override getFirstLabel(widget: Widget): string {
		return widget.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public override getSecondLabel(widget: Widget): string {
		return widget.description;
	}

	/**
	 * {@inheritDoc}
	 */
	public override getThirdLabel(widget: Widget): string {
		return widget.category.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public override getObjectImageURL(widget: Widget): string {
		return HttpAssetService.getContentUrl(widget.imageToken);
	}

	/**
	 * Function used to configure the header of the list component
	 */
	private initHeaderConfiguration(): void {
		this.headerConfiguration = {
			title: 'catalog'
		};
	}

	/**
	 * Init filter for list component
	 */
	private initFilter(): void {
		this.httpFilter.sort = ['category.name,name,asc'];
	}
}
