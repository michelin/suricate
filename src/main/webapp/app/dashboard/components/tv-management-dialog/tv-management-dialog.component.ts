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

import {Component, Inject, Injector, OnDestroy, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';

import { Project } from '../../../shared/models/backend/project/project';
import { WebsocketClient } from '../../../shared/models/backend/websocket-client';
import { HttpScreenService } from '../../../shared/services/backend/http-screen/http-screen.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { FormService } from '../../../shared/services/frontend/form/form.service';
import { TranslateService } from '@ngx-translate/core';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../shared/models/frontend/button/button-configuration';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { CustomValidator } from '../../../shared/validators/custom-validator';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import {Rotation} from "../../../shared/models/backend/rotation/rotation";

/**
 * Component that manage the popup for Dashboard TV Management
 */
@Component({
  template: '',
  styleUrls: ['./tv-management-dialog.component.scss']
})
export abstract class TvManagementDialogComponent implements OnInit {
  /**
   * Service used to help on the form creation
   */
  private readonly formService: FormService;

  /**
   * HTTP screen service
   */
  protected readonly httpScreenService: HttpScreenService

  /**
   * The configuration of the share button
   */
  public shareButtonsConfiguration: ButtonConfiguration<unknown>[] = [];

  /**
   * The configuration of the share button
   */
  public connectedScreenButtonsConfiguration: ButtonConfiguration<WebsocketClient>[] = [];

  /**
   * The register screen form
   */
  public registerScreenCodeFormField: FormGroup;

  /**
   * The description of the form}
   */
  public formFields: FormField[];

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
   * @param injector The injector
   */
  protected constructor(protected readonly injector: Injector) {
    this.formService = injector.get(FormService);
    this.httpScreenService = injector.get(HttpScreenService);

    this.initButtonsConfiguration();
  }

  /**
   * When the component is initialized
   */
  public ngOnInit(): void {
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
        tooltip: { message: 'screen.unsubscribe' },
        callback: (event: Event, websocketClient: WebsocketClient) => this.disconnectScreen(websocketClient)
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
   * Disconnect a screen
   *
   * @param websocketClient The websocket to disconnect
   */
  private disconnectScreen(websocketClient: WebsocketClient): void {
    this.httpScreenService.disconnectScreen(websocketClient.projectToken, +websocketClient.screenCode).subscribe(() => {
      setTimeout(() => this.getConnectedWebsocketClient(), 2000);
    });
  }

  /**
   * Display the screen code on every connected screens
   *
   * @param projectToken The project token
   */
  public displayScreenCode(projectToken: string): void {
    if (projectToken) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForProject(projectToken).subscribe();
    }
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

  /**
   * Register a screen
   */
  abstract registerScreen(): void;

  /**
   * Retrieve the websocket connections
   */
  abstract getConnectedWebsocketClient(): void;
}
