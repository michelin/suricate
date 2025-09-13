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
import { NgClass, NgOptimizedImage, TitleCasePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';

import { Header } from '../../layout/components/header/header';
import { Buttons } from '../../shared/components/buttons/buttons';
import { Input } from '../../shared/components/inputs/input/input';
import { List } from '../../shared/components/list/list';
import { Paginator } from '../../shared/components/paginator/paginator';
import { Spinner } from '../../shared/components/spinner/spinner';
import { ButtonColor } from '../../shared/enums/button-color';
import { Icon } from '../../shared/enums/icon';
import { ToastType } from '../../shared/enums/toast-type';
import { Role } from '../../shared/models/backend/role/role';
import { User } from '../../shared/models/backend/user/user';
import { UserRequest } from '../../shared/models/backend/user/user-request';
import { AbstractHttpService } from '../../shared/services/backend/abstract-http/abstract-http.service';
import { HttpAdminUserService } from '../../shared/services/backend/http-admin-user/http-admin-user.service';
import { UserFormFieldsService } from '../../shared/services/frontend/form-fields/user-form-fields/user-form-fields.service';

/**
 * Component used to display the list of users
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
	providers: [{ provide: AbstractHttpService, useClass: HttpAdminUserService }]
})
export class Users extends List<User, UserRequest> implements OnInit {
	private readonly httpAdminUserService = inject(HttpAdminUserService);
	private readonly userFormFieldsService = inject(UserFormFieldsService);

	/**
	 * User selected in the list for modification
	 */
	private userSelected: User;

	/**
	 * Constructor
	 */
	constructor() {
		super();
		this.initHeaderConfiguration();
		this.initListConfiguration();
		this.initFilter();
	}

	public override ngOnInit(): void {
		super.ngOnInit();
	}

	/**
	 * Function used to configure the header of the list component
	 */
	private initHeaderConfiguration(): void {
		this.headerConfiguration = { title: 'user.list' };
	}

	/**
	 * Function used to init the configuration of the list
	 */
	private initListConfiguration(): void {
		this.listConfiguration = {
			buttons: [
				{
					icon: Icon.EDIT,
					tooltip: { message: 'user.edit' },
					variant: 'miniFab',
					callback: (event: Event, user: User) => this.openFormSidenav(event, user, this.editUser.bind(this))
				},
				{
					icon: Icon.DELETE,
					tooltip: { message: 'user.delete' },
					color: ButtonColor.WARN,
					variant: 'miniFab',
					callback: (event: Event, user: User) => this.deleteUser(event, user)
				}
			]
		};
	}

	/**
	 * Init filter for list component
	 */
	private initFilter(): void {
		this.httpFilter.sort = ['username,asc'];
	}

	/**
	 * {@inheritDoc}
	 */
	protected override getFirstLabel(user: User): string {
		return `${user.firstname} ${user.lastname} (${user.username})`;
	}

	/**
	 * {@inheritDoc}
	 */
	protected override getSecondLabel(user: User): string {
		return user.email;
	}

	/**
	 * {@inheritDoc}
	 */
	protected override getThirdLabel(user: User): string {
		return user.roles.map((role: Role) => role.name).join(', ');
	}

	/**
	 * Open the form sidenav
	 *
	 * @param event The click event
	 * @param user The user clicked on the list
	 * @param saveCallback The function to call when save button is clicked
	 */
	private openFormSidenav(event: Event, user: User, saveCallback: (formGroup: UntypedFormGroup) => void): void {
		this.userSelected = user;

		this.sidenavService.openFormSidenav({
			title: user ? 'user.edit' : 'user.add',
			formFields: this.userFormFieldsService.generateFormFields(user),
			save: (formGroup: UntypedFormGroup) => saveCallback(formGroup)
		});
	}

	/**
	 * Edit a user
	 * @param formGroup The form group
	 */
	private editUser(formGroup: UntypedFormGroup): void {
		const userRequest: UserRequest = formGroup.value;
		this.httpAdminUserService.update(this.userSelected.id, userRequest).subscribe(() => {
			super.refreshList();
		});
	}

	/**
	 * Function used to delete a user
	 * @param event The click event
	 * @param user The user to delete
	 */
	private deleteUser(event: Event, user: User): void {
		const titleCasePipe = new TitleCasePipe();

		this.dialogService.confirm({
			title: 'user.delete',
			message: `${this.translateService.instant('user.delete.confirm')} ${titleCasePipe.transform(user.username)} ?`,
			accept: () => {
				this.httpAdminUserService.delete(user.id).subscribe(() => {
					this.toastService.sendMessage('user.delete.success', ToastType.SUCCESS);
					this.refreshList();
				});
			}
		});
	}
}
