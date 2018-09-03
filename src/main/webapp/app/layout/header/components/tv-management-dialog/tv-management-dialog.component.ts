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

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';

import {DashboardService} from '../../../../modules/dashboard/dashboard.service';
import {Project} from '../../../../shared/model/dto/Project';
import {WebsocketClient} from '../../../../shared/model/dto/WebsocketClient';
import {ScreenService} from '../../../../modules/dashboard/screen.service';

/**
 * Component that manage the popup for Dashboard TV Management
 */
@Component({
  selector: 'app-tv-management-dialog',
  templateUrl: './tv-management-dialog.component.html',
  styleUrls: ['./tv-management-dialog.component.css']
})
export class TvManagementDialogComponent implements OnInit {

  /**
   * The register screen form
   * @type {FormGroup}
   */
  screenRegisterForm: FormGroup;

  /**
   * The current project
   * @type {Project}
   */
  project: Project;

  /**
   * Constructor
   *
   * @param data The data give to the modal
   * @param {FormBuilder} formBuilder The formBuilder
   * @param {DashboardService} dashboardService The dashboard service to inject
   * @param {ScreenService} screenService The screen service
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any,
              private formBuilder: FormBuilder,
              private dashboardService: DashboardService,
              private screenService: ScreenService) {
  }

  /**
   * When the component is initialized
   */
  ngOnInit() {
    this.dashboardService
        .getOneById(this.data.projectId)
        .subscribe(project => this.project = project);

    this.screenRegisterForm = this.formBuilder.group({
      screenCode: ['']
    });
  }

  /**
   * Register the screen
   */
  registerScreen(): void {
    if (this.screenRegisterForm.valid) {
      const screenCode: string = this.screenRegisterForm.get('screenCode').value;
      this.screenService.connectProjectToScreen(this.project.token, +screenCode);
    }
  }

  /**
   * Disconnect a websocket
   *
   * @param {WebsocketClient} websocketClient The websocket to disconnect
   */
  disconnectScreen(websocketClient: WebsocketClient): void {
    this.screenService.disconnectScreen(websocketClient);
  }

  /**
   * Display the screen code on every connected screens
   * @param projectToken The project token
   */
  displayScreenCode(projectToken: string): void {
    if (projectToken) {
      this.screenService.displayScreenCodeEveryConnectedScreensForProject(projectToken);
    }
  }
}
