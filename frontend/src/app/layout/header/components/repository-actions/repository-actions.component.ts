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

import {Component} from '@angular/core';
import {Router} from '@angular/router';

/**
 * Manage repository actions
 */
@Component({
  selector: 'app-repository-actions',
  templateUrl: './repository-actions.component.html',
  styleUrls: ['./repository-actions.component.css']
})
export class RepositoryActionsComponent {

  /**
   * Constructor
   *
   * @param router The router service
   */
  constructor(private router: Router) {
  }

  /**
   * Navigate to the add repository page
   */
  addRepository() {
    this.router.navigate(['/repositories', 'add']);
  }

}
