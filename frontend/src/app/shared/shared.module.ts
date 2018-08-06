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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {StompRService} from '@stomp/ng2-stompjs';
import {NgGridModule} from 'angular2-grid';
import {CustomFormsModule} from 'ng2-validation';
import {ColorPickerModule} from 'ngx-color-picker';

import {TokenInterceptor} from './interceptors/token.interceptor';
import {AuthGuard} from './auth/guards/auth.guard';
import {MaterialModule} from './modules/material.module';
import {WebsocketService} from './services/websocket.service';
import {ToastComponent} from './components/toast/toast.component';
import {ToastService} from './components/toast/toast.service';
import {SafeHtmlPipe} from './pipes/safe-html.pipe';
import {SafeUrlPipe} from './pipes/safe-url.pipe';
import {EnumKeysPipe} from './pipes/enum-keys.pipe';
import {RunScriptsDirective} from './directives/run-scripts.directive';
import {AdminGuard} from './auth/guards/admin.guard';
import {TokenService} from './auth/token.service';
import {TranslateModule} from '@ngx-translate/core';
import {TranslationComponent} from './components/translations/translation.component';
import {MaterialCDKModule} from './modules/metarialCDK.module';
import {SettingsService} from './services/settings.service';
import {ErrorInterceptor} from './interceptors/error.interceptor';

@NgModule({
  imports: [
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
    TranslationComponent
  ],
  exports: [
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
    TranslationComponent
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    AuthGuard,
    AdminGuard,
    WebsocketService,
    ToastService,
    StompRService,
    TokenService,
    SettingsService
  ]
})
export class SharedModule {
}
