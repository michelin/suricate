/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';

import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketClient } from '../../../shared/models/backend/websocket-client';
import { HttpScreenService } from '../../../shared/services/backend/http-screen.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project.service';
import { FormService } from '../../../shared/services/frontend/form.service';
import { TranslateService } from '@ngx-translate/core';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { CustomValidators } from 'ng2-validation';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { IconEnum } from '../../../shared/enums/icon.enum';

/**
 * Component that manage the popup for Dashboard TV Management
 */
@Component({
  selector: 'suricate-tv-management-dialog',
  templateUrl: './tv-management-dialog.component.html',
  styleUrls: ['./tv-management-dialog.component.scss']
})
export class TvManagementDialogComponent implements OnInit {
  /**
   * The configuration of the share button
   * @type {ButtonConfiguration[]}
   * @protected
   */
  protected shareButtonsConfiguration: ButtonConfiguration<unknown>[] = [];
  /**
   * The configuration of the share button
   * @type {ButtonConfiguration[]}
   * @protected
   */
  protected connectedScreenButtonsConfiguration: ButtonConfiguration<WebsocketClient>[] = [];
  /**
   * The register screen form
   * @type {FormGroup}
   * @protected
   */
  protected screenRegisterForm: FormGroup;

  /**
   * The description of the form
   * @type {FormField[]}
   */
  protected formFields: FormField[];

  /**
   * The current project
   * @type {Project}
   * @protected
   */
  protected project: Project;

  /**
   * The list of clients connected by websocket
   * @type {WebsocketClient[]}
   * @protected
   */
  protected websocketClients: WebsocketClient[];

  /**
   * Constructor
   *
   * @param data Angular service used to inject data in the modal
   * @param {TranslateService} translateService NgxTranslate service used to manage translations
   * @param {HttpProjectService} httpProjectService Suricate service used to manage HTTP calls for project
   * @param {HttpScreenService} httpScreenService Suricate service used to manage HTTP calls for screens
   * @param {FormService} formService Frontend service used to help on form creation

   */
  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly data: { project: Project },
    private readonly httpProjectService: HttpProjectService,
    private readonly httpScreenService: HttpScreenService,
    private readonly translateService: TranslateService,
    private readonly formService: FormService
  ) {
    this.initButtonsConfiguration();
  }

  /**
   * Init the buttons configurations
   */
  private initButtonsConfiguration(): void {
    this.shareButtonsConfiguration = [
      {
        icon: IconEnum.SHARE_SCREEN,
        color: 'primary',
        type: ButtonTypeEnum.SUBMIT,
        tooltip: { message: 'Register' }
      }
    ];

    this.connectedScreenButtonsConfiguration = [
      {
        icon: IconEnum.STOP_SHARE_SCREEN,
        color: 'primary',
        type: ButtonTypeEnum.BUTTON,
        tooltip: { message: 'Unsubscribe' },
        callback: (event: Event, websocketClient: WebsocketClient) => this.disconnectScreen(websocketClient)
      }
    ];
  }

  /**
   * When the component is initialized
   */
  public ngOnInit(): void {
    this.project = this.data.project;
    this.getConnectedWebsocketClient();
    this.generateFormFields();

    this.screenRegisterForm = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Generate the form fields form screen subscriptions
   */
  private generateFormFields(): void {
    this.formFields = [
      {
        key: 'screenCode',
        label: 'screen.field.code',
        type: DataTypeEnum.NUMBER,
        validators: [CustomValidators.digits, CustomValidators.gt(0)]
      }
    ];
  }

  /**
   * Retrieve the websocket connections
   */
  private getConnectedWebsocketClient(): void {
    this.httpProjectService.getProjectWebsocketClients(this.project.token).subscribe(websocketClients => {
      this.websocketClients = websocketClients;
    });
  }

  /**
   * Register a screen
   */
  protected registerScreen(): void {
    if (this.screenRegisterForm.valid) {
      const screenCode: string = this.screenRegisterForm.get('screenCode').value;

      this.httpScreenService.connectProjectToScreen(this.project.token, +screenCode).subscribe(() => {
        this.screenRegisterForm.reset();
        setTimeout(() => this.getConnectedWebsocketClient(), 2000);
      });
    }
  }

  /**
   * Disconnect a screen
   *
   * @param {WebsocketClient} websocketClient The websocket to disconnect
   */
  private disconnectScreen(websocketClient: WebsocketClient): void {
    this.httpScreenService.disconnectScreen(websocketClient.projectToken, +websocketClient.screenCode).subscribe(() => {
      setTimeout(() => this.getConnectedWebsocketClient(), 2000);
    });
  }

  /**
   * Display the screen code on every connected screens
   *
   * @param {string} projectToken The project token
   */
  protected displayScreenCode(projectToken: string): void {
    if (projectToken) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForProject(projectToken).subscribe();
    }
  }
}
