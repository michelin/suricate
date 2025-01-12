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

import { CommonModule, DatePipe, NgOptimizedImage } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { KtdGridModule } from '@katoid/angular-grid-layout';
import { TranslateModule } from '@ngx-translate/core';
import { ColorPickerModule } from 'ngx-color-picker';

import { ActionsDialogComponent } from './components/actions-dialog/actions-dialog.component';
import { ButtonsComponent } from './components/buttons/buttons.component';
import { CommunicationDialogComponent } from './components/communication-dialog/communication-dialog.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { FormSidenavComponent } from './components/form-sidenav/form-sidenav.component';
import { CheckboxComponent } from './components/inputs/checkbox/checkbox.component';
import { ColorPickerComponent } from './components/inputs/color-picker/color-picker.component';
import { FieldsComponent } from './components/inputs/fields/fields.component';
import { FileInputComponent } from './components/inputs/file-input/file-input.component';
import { InputComponent } from './components/inputs/input/input.component';
import { MosaicComponent } from './components/inputs/mosaic/mosaic.component';
import { SlideToggleComponent } from './components/inputs/slide-toggle/slide-toggle.component';
import { ListComponent } from './components/list/list.component';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { ProgressBarComponent } from './components/progress-bar/progress-bar.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { ToastComponent } from './components/toast/toast.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { HideAfterInitDirective } from './directives/hide-after-init/hide-after-init.directive';
import { WidgetHtmlDirective } from './directives/widget-html/widget-html.directive';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { MaterialModule } from './modules/material.module';
import { MaterialCDKModule } from './modules/material-cdk.module';
import { SafeHtmlPipe } from './pipes/safe-html/safe-html.pipe';

@NgModule({
  declarations: [
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    FileInputComponent,
    InputComponent,
    CheckboxComponent,
    WidgetHtmlDirective,
    HideAfterInitDirective,
    SafeHtmlPipe,
    ToastComponent,
    ListComponent,
    SpinnerComponent,
    ButtonsComponent,
    FormSidenavComponent,
    WizardComponent,
    ColorPickerComponent,
    FieldsComponent,
    MosaicComponent,
    PaginatorComponent,
    SlideToggleComponent,
    ProgressBarComponent,
    ActionsDialogComponent
  ],
  exports: [
    BrowserAnimationsModule,
    BrowserModule,
    CheckboxComponent,
    CommonModule,
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    FileInputComponent,
    FormsModule,
    InputComponent,
    MaterialCDKModule,
    MaterialModule,
    KtdGridModule,
    ReactiveFormsModule,
    RouterModule,
    WidgetHtmlDirective,
    HideAfterInitDirective,
    SafeHtmlPipe,
    ToastComponent,
    TranslateModule,
    SpinnerComponent,
    ButtonsComponent,
    FormSidenavComponent,
    ListComponent,
    WizardComponent,
    ColorPickerComponent,
    PaginatorComponent,
    SlideToggleComponent,
    ProgressBarComponent,
    ColorPickerModule,
    NgOptimizedImage
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    CommonModule,
    FormsModule,
    MaterialCDKModule,
    MaterialModule,
    KtdGridModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule,
    ColorPickerModule,
    NgOptimizedImage
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    DatePipe,
    provideHttpClient(withInterceptorsFromDi())
  ]
})
export class SharedModule {}
