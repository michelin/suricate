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
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgGridModule } from 'angular2-grid';
import { CustomFormsModule } from 'ng2-validation';
import { ColorPickerModule } from 'ngx-color-picker';

import { TokenInterceptor } from './interceptors/token.interceptor';
import { MaterialModule } from './modules/material.module';
import { ToastComponent } from './components/toast/toast.component';
import { SafeHtmlPipe } from './pipes/safe-html.pipe';
import { SafeUrlPipe } from './pipes/safe-url.pipe';
import { EnumKeysPipe } from './pipes/enum-keys.pipe';
import { RunScriptsDirective } from './directives/run-scripts.directive';
import { TranslateModule } from '@ngx-translate/core';
import { TranslationComponent } from './components/translations/translation.component';
import { MaterialCDKModule } from './modules/metarialCDK.module';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { PasswordPipe } from './pipes/password-pipe';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { CommunicationDialogComponent } from './components/communication-dialog/communication-dialog.component';
import { InputComponent } from './components/form/inputs/input.component';
import { CheckboxComponent } from './components/form/inputs/checkbox/checkbox.component';
import { FileInputComponent } from './components/form/inputs/file-input/file-input.component';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgGridModule,
    MaterialModule,
    MaterialCDKModule,
    FlexLayoutModule,
    RouterModule,
    CustomFormsModule,
    ColorPickerModule,
    TranslateModule
  ],
  declarations: [
    ToastComponent,
    SafeHtmlPipe,
    SafeUrlPipe,
    EnumKeysPipe,
    RunScriptsDirective,
    TranslationComponent,
    PasswordPipe,
    ConfirmDialogComponent,
    CommunicationDialogComponent,
    InputComponent,
    CheckboxComponent,
    FileInputComponent
  ],
  exports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgGridModule,
    MaterialModule,
    MaterialCDKModule,
    FlexLayoutModule,
    RouterModule,
    CustomFormsModule,
    ColorPickerModule,
    TranslateModule,
    ToastComponent,
    SafeHtmlPipe,
    SafeUrlPipe,
    EnumKeysPipe,
    RunScriptsDirective,
    TranslationComponent,
    PasswordPipe,
    ConfirmDialogComponent,
    CommunicationDialogComponent,
    InputComponent,
    CheckboxComponent,
    FileInputComponent
  ],
  entryComponents: [ConfirmDialogComponent, CommunicationDialogComponent],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ]
})
export class SharedModule {}
