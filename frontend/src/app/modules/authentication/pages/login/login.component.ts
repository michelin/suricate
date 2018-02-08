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

import {Component, OnInit, ViewChild} from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';

import { AuthenticationService} from '../../../../shared/services/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;

  hidePassword = true;
  @ViewChild('formSubmitAttempt') formSubmitAttempt = false;

  constructor(private router: Router, private authenticationService: AuthenticationService, private formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.group({
      'username': ['', Validators.required],
      'password': ['', Validators.required]
    });
  }

  ngOnInit() {
    this.authenticationService.logout();
  }

  isFieldInvalid(field: string) {
    return (
      (this.loginForm.get(field).touched && !this.loginForm.get(field).valid) ||
      (this.loginForm.get(field).untouched && this.formSubmitAttempt)
    );
  }

  login() {
    if (this.loginForm.valid) {
      this.formSubmitAttempt = true;

      this.authenticationService
        .login(this.loginForm.value)
        .subscribe (
          data => {
            this.router.navigate(['/home']);
          },
          error => {
            this.formSubmitAttempt = false;
            console.log(error);
          });
    }
  }
}
