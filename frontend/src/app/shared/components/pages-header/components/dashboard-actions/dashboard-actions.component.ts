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

import { Component, OnInit } from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AddWidgetDialogComponent} from '../add-widget-dialog/add-widget-dialog.component';
import {ActivatedRoute} from '@angular/router';
import {AddDashboardDialogComponent} from '../add-dashboard-dialog/add-dashboard-dialog.component';

@Component({
  selector: 'app-dashboard-actions',
  templateUrl: './dashboard-actions.component.html',
  styleUrls: ['./dashboard-actions.component.css']
})
export class DashboardActionsComponent implements OnInit {

  addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;
  editWidgetDialogRef: MatDialogRef<AddDashboardDialogComponent>;
  projectId: number;

  constructor(private dialog: MatDialog,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.projectId = params['id'];
    });
  }

  openAddWidgetDialog() {
    this.addWidgetDialogRef = this.dialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      data: { projectId: this.projectId}
    });
  }

  openEditDashboardDialog() {
    this.editWidgetDialogRef = this.dialog.open(AddDashboardDialogComponent, {
      minWidth: 900,
      data: { projectId: this.projectId }
    });
  }
}
