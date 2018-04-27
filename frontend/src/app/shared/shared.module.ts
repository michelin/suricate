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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgGridModule} from 'angular2-grid';
import {TokenInterceptor} from './interceptors/TokenInterceptor';
import {AuthGuard} from './guards/auth.guard';
import {MaterialModule} from './modules/material.module';
import {PagesHeaderComponent} from './components/pages-header/pages-header.component';
import {AddWidgetDialogComponent} from './components/pages-header/components/add-widget-dialog/add-widget-dialog.component';
import {FlexLayoutModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {DashboardActionsComponent} from './components/pages-header/components/dashboard-actions/dashboard-actions.component';
import { HomeActionsComponent } from './components/pages-header/components/home-actions/home-actions.component';
import { AddDashboardDialogComponent } from './components/pages-header/components/add-dashboard-dialog/add-dashboard-dialog.component';
import { CustomFormsModule} from 'ng2-validation';
import {ColorPickerModule} from 'ngx-color-picker';
import {StompService} from 'ng2-stomp-service/index';
import {WebsocketService} from './services/websocket.service';
import { ToastComponent } from './components/toast/toast.component';
import {ToastService} from './components/toast/toast.service';

@NgModule({
  imports: [
      CommonModule,
      FormsModule,
      ReactiveFormsModule,
      HttpClientModule,
      NgGridModule,
      MaterialModule,
      FlexLayoutModule,
      RouterModule,
      CustomFormsModule,
      ColorPickerModule
  ],
  declarations: [
      PagesHeaderComponent,
      AddWidgetDialogComponent,
      DashboardActionsComponent,
      HomeActionsComponent,
      AddDashboardDialogComponent,
      ToastComponent
  ],
  entryComponents: [
      AddWidgetDialogComponent,
      AddDashboardDialogComponent
  ],
  exports: [
      FormsModule,
      ReactiveFormsModule,
      HttpClientModule,
      NgGridModule,
      MaterialModule,
      FlexLayoutModule,
      RouterModule,
      CustomFormsModule,
      ColorPickerModule,
      PagesHeaderComponent,
      AddWidgetDialogComponent,
      ToastComponent
  ],
  providers: [
      { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
      AuthGuard,
      StompService,
      WebsocketService,
      ToastService
  ]
})
export class SharedModule { }
