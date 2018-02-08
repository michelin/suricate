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

import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AddWidgetDialogComponent} from './components/add-widget-dialog/add-widget-dialog.component';

@Component({
  selector: 'app-pages-header',
  templateUrl: './pages-header.component.html',
  styleUrls: ['./pages-header.component.css']
})
export class PagesHeaderComponent implements OnInit {
  addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;
  pageName: string;
  projectId: number;

  @Input('secondTitle') secondTitle: string;

  constructor(private route: Router,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.projectId = params['id'];
    });

    this.pageName = this.route.url.split('/')[1];
  }

  openAddWidgetDialog() {
    this.addWidgetDialogRef = this.dialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      data: { projectId: this.projectId}
    });
  }
}
