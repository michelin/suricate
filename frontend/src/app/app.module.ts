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
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { NgGridModule } from 'angular2-grid';

import { MaterialDesignModule } from './shared/modules/material-design.module';

import { AuthGuard } from './shared/guards/auth.guard';

import { TokenInterceptor } from './shared/interceptors/TokenInterceptor';

import { AuthenticationService } from './shared/services/authentication.service';
import { UserService } from './shared/services/user.service';

import { AppComponent } from './app.component';
import { LoginComponent } from './modules/authentication/pages/login/login.component';
import { SidenavComponent } from './shared/layouts/sidenav/sidenav.component';
import { PagesHeaderComponent } from './shared/layouts/pages-header/pages-header.component';
import { UserListComponent } from './modules/user/pages/user-list/user-list.component';
import { UserModule } from './modules/user/user.module';
import { DashboardModule } from './modules/dashboard/dashboard.module';
import { DashboardDetailComponent } from './modules/dashboard/pages/dashboard-detail/dashboard-detail.component';
import { HomeComponent } from './modules/home/home.component';
import { AddWidgetDialogComponent } from './shared/layouts/pages-header/components/add-widget-dialog/add-widget-dialog.component';
import { WidgetService } from './shared/services/widget.service';
import {HeaderDashboardSharedService} from './shared/services/header-dashboard-shared.service';

const appRoutes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent }
];


@NgModule({
  imports: [
      BrowserModule,
      BrowserAnimationsModule,
      RouterModule.forRoot(appRoutes),
      FormsModule,
      ReactiveFormsModule,
      HttpClientModule,
      MaterialDesignModule,
      NgGridModule,
      UserModule,
      DashboardModule
  ],
  declarations: [
      AppComponent,
      LoginComponent,
      SidenavComponent,
      PagesHeaderComponent,

      DashboardDetailComponent,

      UserListComponent,
      HomeComponent,
      AddWidgetDialogComponent
  ],
  entryComponents: [
      AddWidgetDialogComponent
  ],
  providers: [
      { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
      AuthGuard,
      AuthenticationService,
      UserService,
      WidgetService,
      HeaderDashboardSharedService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
