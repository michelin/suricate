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

import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { RxStompConfig, StompHeaders } from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root'
})
export class MockRxStompService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Mocked initAndConnect method for the unit tests
   */
  public activate(): void {}

  /**
   * Mocked subscribe method for the unit tests
   *
   * @param queueName The name of the queue
   * @param headers Any optional headers
   */
  public watch(queueName: string, headers?: StompHeaders): Observable<void> {
    return of();
  }

  /**
   * Mocked disconnect method for the unit tests
   */
  public deactivate(): Promise<void> {
    return Promise.resolve();
  }

  /**
   * Mocked configure method for the unit tests
   *
   * @param rxStompConfig The configuration
   */
  public configure(rxStompConfig: RxStompConfig): void {}
}
