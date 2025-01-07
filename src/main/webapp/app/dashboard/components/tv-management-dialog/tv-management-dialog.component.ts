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

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UntypedFormGroup } from '@angular/forms';

import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketClient } from '../../../shared/models/backend/websocket-client';
import { HttpScreenService } from '../../../shared/services/backend/http-screen/http-screen.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { CustomValidator } from '../../../shared/validators/custom-validator';

@Component({
  selector: 'suricate-tv-management-dialog',
  templateUrl: './tv-management-dialog.component.html',
  styleUrls: ['./tv-management-dialog.component.scss']
})
export class TvManagementDialogComponent implements OnInit {
  /**
   * The configuration of the share button
   */
  public shareButtonsConfiguration: ButtonConfiguration<unknown>[] = [];

  /**
   * The configuration of the share button
   */
  public connectedScreenButtonsConfiguration: ButtonConfiguration<WebsocketClient>[] = [];

  /**
   * The configuration of the generic window buttons
   */
  public genericButtonsConfiguration: ButtonConfiguration<WebsocketClient>[] = [];

  /**
   * The register screen form
   */
  public registerScreenCodeFormField: UntypedFormGroup;

  /**
   * The description of the form
   */
  public formFields: FormField[];

  /**
   * The current project
   */
  public project: Project;

  /**
   * The list of clients connected by websocket
   */
  public websocketClients: WebsocketClient[];

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param data Angular service used to inject data in the modal
   * @param httpProjectService Suricate service used to manage HTTP calls for project
   * @param httpScreenService Suricate service used to manage HTTP calls for screens
   * @param formService Frontend service used to help on form creation
   */
  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly data: { project: Project },
    private readonly httpProjectService: HttpProjectService,
    private readonly httpScreenService: HttpScreenService,
    private readonly formService: FormService
  ) {
    this.initButtonsConfiguration();
  }

  /**
   * When the component is initialized
   */
  public ngOnInit(): void {
    this.project = this.data.project;
    this.getConnectedWebsocketClient();
    this.generateFormFields();

    this.registerScreenCodeFormField = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Init the buttons configurations
   */
  private initButtonsConfiguration(): void {
    this.shareButtonsConfiguration = [
      {
        icon: IconEnum.SHARE_SCREEN,
        color: 'primary',
        variant: 'miniFab',
        type: ButtonTypeEnum.SUBMIT,
        tooltip: { message: 'screen.subscribe' },
        callback: () => this.validateFormBeforeSave()
      }
    ];

    this.connectedScreenButtonsConfiguration = [
      {
        icon: IconEnum.STOP_SHARE_SCREEN,
        color: 'primary',
        type: ButtonTypeEnum.BUTTON,
        variant: 'miniFab',
        tooltip: { message: 'screen.unsubscribe' },
        callback: (event: Event, websocketClient: WebsocketClient) => this.disconnectScreen(websocketClient)
      }
    ];

    this.genericButtonsConfiguration = [
      {
        label: 'close',
        icon: IconEnum.CLOSE,
        color: 'warn'
      }
    ];
  }

  /**
   * Generate the form fields form screen subscriptions
   */
  private generateFormFields(): void {
    this.formFields = [
      {
        key: 'screenCode',
        label: 'screen.code',
        type: DataTypeEnum.NUMBER,
        validators: [CustomValidator.isDigits, CustomValidator.greaterThan0]
      }
    ];
  }

  /**
   * Retrieve the websocket connections to a dashboard
   */
  public getConnectedWebsocketClient(): void {
    this.httpProjectService.getProjectWebsocketClients(this.project.token).subscribe(websocketClients => {
      this.websocketClients = websocketClients;
    });
  }

  /**
   * Register a screen
   */
  public registerScreen(): void {
    if (this.registerScreenCodeFormField.valid) {
      const screenCode: string = this.registerScreenCodeFormField.get('screenCode').value;

      this.httpScreenService.connectProjectToScreen(this.project.token, +screenCode).subscribe(() => {
        this.registerScreenCodeFormField.reset();
        setTimeout(() => this.getConnectedWebsocketClient(), 2000);
      });
    }
  }

  /**
   * Display the screen code on every connected screens
   */
  public displayScreenCode(): void {
    if (this.project.token) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForProject(this.project.token).subscribe();
    }
  }

  /**
   * Disconnect a screen
   *
   * @param websocketClient The websocket to disconnect
   */
  public disconnectScreen(websocketClient: WebsocketClient): void {
    this.httpScreenService.disconnectScreenFromProject(websocketClient.projectToken, +websocketClient.screenCode).subscribe(() => {
      setTimeout(() => this.getConnectedWebsocketClient(), 2000);
    });
  }

  /**
   * Check if the stepper form is valid before saving the data
   */
  protected validateFormBeforeSave(): void {
    this.formService.validate(this.registerScreenCodeFormField);

    if (this.registerScreenCodeFormField.valid) {
      this.registerScreen();
    }
  }
}
