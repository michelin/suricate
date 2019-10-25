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

import { ColorPickerModule } from 'ngx-color-picker';
import { CommonModule } from '@angular/common';
import { CustomFormsModule } from 'ng2-validation';
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
import { EnumKeysPipe } from './pipes/enum-keys.pipe';
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
import { TranslationComponent } from './components/translations/translation.component';
import { StompRService } from '@stomp/ng2-stompjs';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    ColorPickerModule,
    CommonModule,
    CustomFormsModule,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    HttpClientModule,
    MaterialCDKModule,
    MaterialModule,
    NgGridModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule
  ],
  declarations: [
    CheckboxComponent,
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    EnumKeysPipe,
    FileInputComponent,
    InputComponent,
    PasswordPipe,
    RunScriptsDirective,
    SafeHtmlPipe,
    SafeUrlPipe,
    ToastComponent,
    TranslationComponent
  ],
  exports: [
    BrowserAnimationsModule,
    BrowserModule,
    CheckboxComponent,
    ColorPickerModule,
    CommonModule,
    CommunicationDialogComponent,
    ConfirmDialogComponent,
    CustomFormsModule,
    EnumKeysPipe,
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
    TranslationComponent
  ],
  entryComponents: [CommunicationDialogComponent, ConfirmDialogComponent],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    StompRService
  ]
})
export class SharedModule {}
