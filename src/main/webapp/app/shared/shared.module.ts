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

import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgGridModule } from 'angular2-grid';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { CheckboxComponent } from './components/inputs/checkbox/checkbox.component';
import { CommunicationDialogComponent } from './components/communication-dialog/communication-dialog.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { FileInputComponent } from './components/inputs/file-input/file-input.component';
import { InputComponent } from './components/inputs/input.component';
import { MaterialCDKModule } from './modules/material-cdk.module';
import { MaterialModule } from './modules/material.module';
import { PasswordPipe } from './pipes/password.pipe';
import { RunScriptsDirective } from './directives/run-scripts.directive';
import { SafeHtmlPipe } from './pipes/safe-html.pipe';
import { SafeUrlPipe } from './pipes/safe-url.pipe';
import { ToastComponent } from './components/toast/toast.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { TranslateModule } from '@ngx-translate/core';
import { StompRService } from '@stomp/ng2-stompjs';
import { ListComponent } from './components/list/list.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { ButtonsComponent } from './components/buttons/buttons.component';
import { FormSidenavComponent } from './components/form-sidenav/form-sidenav.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { ColorPickerModule } from 'ngx-color-picker';
import { ColorPickerComponent } from './components/inputs/color-picker/color-picker.component';
import { FieldsComponent } from './components/inputs/fields/fields.component';
import { MosaicComponent } from './components/inputs/mosaic/mosaic.component';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { SlideToggleComponent } from './components/inputs/slide-toggle/slide-toggle.component';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    CommonModule,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    HttpClientModule,
    MaterialCDKModule,
    MaterialModule,
    NgGridModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule,
    ColorPickerModule
  ],
  declarations: [
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    FileInputComponent,
    InputComponent,
    CheckboxComponent,
    PasswordPipe,
    RunScriptsDirective,
    SafeHtmlPipe,
    SafeUrlPipe,
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
    SlideToggleComponent
  ],
  exports: [
    BrowserAnimationsModule,
    BrowserModule,
    CheckboxComponent,
    CommonModule,
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    FileInputComponent,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    HttpClientModule,
    InputComponent,
    MaterialCDKModule,
    MaterialModule,
    NgGridModule,
    PasswordPipe,
    ReactiveFormsModule,
    RouterModule,
    RunScriptsDirective,
    SafeHtmlPipe,
    SafeUrlPipe,
    ToastComponent,
    TranslateModule,
    SpinnerComponent,
    ButtonsComponent,
    FormSidenavComponent,
    ListComponent,
    WizardComponent,
    ColorPickerComponent,
    PaginatorComponent,
    SlideToggleComponent
  ],
  entryComponents: [CommunicationDialogComponent, ConfirmDialogComponent],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    StompRService
  ]
})
export class SharedModule {}
