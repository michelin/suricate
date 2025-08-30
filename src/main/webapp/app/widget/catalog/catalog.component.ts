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

import { HeaderComponent } from '../../layout/components/header/header.component';
import { ButtonsComponent } from '../../shared/components/buttons/buttons.component';
import { InputComponent } from '../../shared/components/inputs/input/input.component';
import { ListComponent } from '../../shared/components/list/list.component';
import { PaginatorComponent } from '../../shared/components/paginator/paginator.component';
import { SpinnerComponent } from '../../shared/components/spinner/spinner.component';
import { Widget } from '../../shared/models/backend/widget/widget';
import { WidgetRequest } from '../../shared/models/backend/widget/widget-request';
import { AbstractHttpService } from '../../shared/services/backend/abstract-http/abstract-http.service';
import { HttpAssetService } from '../../shared/services/backend/http-asset/http-asset.service';
import { HttpWidgetService } from '../../shared/services/backend/http-widget/http-widget.service';

/**
 * Component used to display the list of widgets
 */
@Component({
	templateUrl: '../../shared/components/list/list.component.html',
	styleUrls: ['../../shared/components/list/list.component.scss'],
	imports: [
		HeaderComponent,
		InputComponent,
		FormsModule,
		ReactiveFormsModule,
		SpinnerComponent,
		CdkDropList,
		CdkDrag,
		NgClass,
		NgOptimizedImage,
		ButtonsComponent,
		PaginatorComponent
	],
	providers: [{ provide: AbstractHttpService, useClass: HttpWidgetService }]
})
export class CatalogComponent extends ListComponent<Widget, WidgetRequest> {
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
