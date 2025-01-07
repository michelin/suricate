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
import { BehaviorSubject } from 'rxjs';

/**
 * Service managing the external JS libraries used by the widgets
 */
@Injectable({ providedIn: 'root' })
export class LibraryService {
  /**
   * List of JS libraries to load for the current project
   */
  public loadedExternalLibraries: string[] = [];

  /**
   * Number of JS libraries to load for the current project
   */
  public numberOfExternalLibrariesToLoad: number;

  /**
   * Are the required JS libraries loaded
   */
  public allExternalLibrariesLoaded = new BehaviorSubject<boolean>(false);

  /**
   * Initialize the service
   */
  public init(numberOfExternalLibrariesToLoad: number) {
    this.loadedExternalLibraries = [];
    this.numberOfExternalLibrariesToLoad = numberOfExternalLibrariesToLoad;
  }

  /**
   * Emit a new event according to the given value
   *
   * @param value The new value to emit
   */
  public emitAreJSScriptsLoaded(value: boolean) {
    this.allExternalLibrariesLoaded.next(value);
  }

  /**
   * Callback function called when a JS library has been injected in the DOM and loaded from the Back-End.
   * Mark the library linked with the given token as loaded.
   * Then, if all the required JS libraries have been loaded, then emit an event to the subscribers.
   *
   * @param token A token representing a JS library
   */
  public markScriptAsLoaded(token: string) {
    if (this.loadedExternalLibraries.indexOf(token) === -1) {
      this.loadedExternalLibraries.push(token);
    }

    if (this.loadedExternalLibraries.length === this.numberOfExternalLibrariesToLoad) {
      this.emitAreJSScriptsLoaded(true);
    }
  }
}
