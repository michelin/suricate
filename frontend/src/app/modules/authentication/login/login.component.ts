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

import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../authentication.service';

/**
 * Manage the login page
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  /**
   * The login form
   */
  loginForm: FormGroup;

  /**
   * If the password field is hidden or not
   *
   * @type {boolean}
   */
  hidePassword = true;

  /**
   * Used for display spinner when form has been submitted
   *
   * @type {boolean}
   */
  formSubmitAttempt = false;

  /**
   * Constructor
   *
   * @param {Router} router The router service
   * @param {AuthenticationService} authenticationService The authentication service
   * @param {FormBuilder} formBuilder The form builder service
   */
  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private formBuilder: FormBuilder) {
  }

  /**
   * Init objects
   */
  ngOnInit() {
    this.authenticationService.logout();

    this.loginForm = this.formBuilder.group({
      'username': ['', [Validators.required]],
      'password': ['', [Validators.required]]
    });
  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.loginForm.invalid && (this.loginForm.get(field).dirty || this.loginForm.get(field).touched);
  }

  /**
   * Execute login action
   */
  login() {
    if (this.loginForm.valid) {
      // Display spinner
      this.formSubmitAttempt = true;

      // Try to authenticate
      this.authenticationService
          .authenticate(this.loginForm.value)
          .subscribe(
              () => {
                // Authentication succeed
                this.router.navigate(['/home']);
              },
              error => {
                // Authentication failed
                this.formSubmitAttempt = false;
                console.log(error);
              });
    }
  }
}
