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
import { RxStompConfig, StompHeaders } from '@stomp/rx-stomp';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MockRxStompService {
  /**
   * Mocked initAndConnect method for the unit tests
   */
  public activate(): void {
    // Mock, no implementation
  }

  /**
   * Mocked subscribe method for the unit tests
   *
   * @param ignoredQueueName The name of the queue
   * @param ignoredHeaders Any optional headers
   */
  public watch(ignoredQueueName: string, ignoredHeaders?: StompHeaders): Observable<void> {
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
   * @param ignoredRxStompConfig The configuration
   */
  public configure(ignoredRxStompConfig: RxStompConfig): void {
    // Mock, no implementation
  }
}
