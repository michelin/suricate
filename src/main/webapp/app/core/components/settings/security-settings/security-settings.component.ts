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

import { Clipboard } from '@angular/cdk/clipboard';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardActions, MatCardContent, MatCardTitle } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { ButtonsComponent } from '../../../../shared/components/buttons/buttons.component';
import { InputComponent } from '../../../../shared/components/inputs/input/input.component';
import { ButtonColorEnum } from '../../../../shared/enums/button-color.enum';
import { DataTypeEnum } from '../../../../shared/enums/data-type.enum';
import { IconEnum } from '../../../../shared/enums/icon.enum';
import { ToastTypeEnum } from '../../../../shared/enums/toast-type.enum';
import { PersonalAccessToken } from '../../../../shared/models/backend/personal-access-token/personal-access-token';
import { PersonalAccessTokenRequest } from '../../../../shared/models/backend/personal-access-token/personal-access-token-request';
import { ButtonConfiguration } from '../../../../shared/models/frontend/button/button-configuration';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { MaterialIconRecords } from '../../../../shared/records/material-icon.record';
import { HttpUserService } from '../../../../shared/services/backend/http-user/http-user.service';
import { DialogService } from '../../../../shared/services/frontend/dialog/dialog.service';
import { FormService } from '../../../../shared/services/frontend/form/form.service';
import { ToastService } from '../../../../shared/services/frontend/toast/toast.service';

@Component({
  selector: 'suricate-security-settings',
  templateUrl: './security-settings.component.html',
  styleUrls: ['./security-settings.component.scss'],
  imports: [
    MatCard,
    MatCardTitle,
    MatIcon,
    MatCardContent,
    MatCardActions,
    MatButton,
    InputComponent,
    FormsModule,
    ReactiveFormsModule,
    ButtonsComponent,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    DatePipe,
    TranslatePipe
  ]
})
export class SecuritySettingsComponent implements OnInit {
  /**
   * The columns of the token table
   */
  public tokenTableColumns: string[] = ['name', 'created', 'revoke'];

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The form group for UX settings
   */
  public formGroup: UntypedFormGroup;

  /**
   * The form fields for UX settings
   */
  public formFields: FormField[] = [];

  /**
   * The generate token button
   */
  public generateTokenButton: ButtonConfiguration<unknown>;

  /**
   * The revoke button
   */
  public revokeButton: ButtonConfiguration<PersonalAccessToken>;

  /**
   * The created token
   */
  public createdToken: PersonalAccessToken;

  /**
   * The user tokens
   */
  public tokens: PersonalAccessToken[];

  /**
   * Constructor
   * @param formService The form service
   * @param toastService The toast service
   * @param translateService The translate service
   * @param httpUserService The http user service
   * @param dialogService The dialog service
   * @param clipboard The clipboard service
   */
  constructor(
    private readonly formService: FormService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService,
    private readonly httpUserService: HttpUserService,
    private readonly dialogService: DialogService,
    private readonly clipboard: Clipboard
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initButtons();
    this.initFormFields();
    this.reloadTokens();
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.generateTokenButton = {
      label: 'settings.security.generate.tokens.button.label',
      icon: IconEnum.SAVE,
      callback: () => this.save()
    };

    this.revokeButton = {
      color: ButtonColorEnum.WARN,
      tooltip: { message: 'revoke' },
      variant: 'miniFab',
      icon: IconEnum.DELETE,
      callback: (event: Event, token: PersonalAccessToken) => this.revokeToken(token)
    };
  }

  /**
   * Init the security settings form fields
   */
  private initFormFields(): void {
    this.formFields.push({
      key: 'name',
      label: 'settings.security.token.name.field',
      type: DataTypeEnum.TEXT,
      validators: [Validators.required]
    });

    this.formGroup = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Reload the user tokens
   */
  private reloadTokens(): void {
    this.httpUserService.getUserTokens().subscribe((tokens: PersonalAccessToken[]) => {
      this.tokens = tokens;
    });
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.formService.validate(this.formGroup);

    if (this.formGroup.valid) {
      const tokenRequest: PersonalAccessTokenRequest = this.formGroup.value;
      this.httpUserService.createToken(tokenRequest).subscribe({
        next: (token: PersonalAccessToken) => {
          this.createdToken = token;
          this.reloadTokens();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 400) {
            this.toastService.sendMessage(
              this.translateService.instant('settings.security.token.created.duplicated.name', {
                tokenName: tokenRequest.name
              }),
              ToastTypeEnum.DANGER
            );
          }
        }
      });
    }
  }

  /**
   * Copy the generated token to clipboard
   */
  public copy(): void {
    const copied = this.clipboard.copy(this.createdToken.value);

    if (copied) {
      this.toastService.sendMessage('copy.success', ToastTypeEnum.SUCCESS);
    }
  }

  /**
   * Revoke the given token
   */
  public revokeToken(token: PersonalAccessToken): void {
    this.dialogService.confirm({
      title: 'token.delete',
      message: this.translateService.instant('token.delete.confirm', { tokenName: token.name }),
      accept: () => {
        this.httpUserService.revokeToken(token.name).subscribe(() => {
          this.toastService.sendMessage('token.delete.success', ToastTypeEnum.SUCCESS);
          this.reloadTokens();
        });
      }
    });
  }
}
