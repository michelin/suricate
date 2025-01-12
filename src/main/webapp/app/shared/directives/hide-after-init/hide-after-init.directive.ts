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

import { AfterViewInit, Directive, ElementRef, Input, OnChanges, SimpleChanges } from '@angular/core';

@Directive({ selector: '[hideAfterInit]' })
export class HideAfterInitDirective implements OnChanges, AfterViewInit {
  /**
   * True if the element should be hidden
   */
  @Input()
  public hide: boolean;

  /**
   * Constructor
   * @param elementRef The reference to the element where the directive is set
   */
  constructor(private readonly elementRef: ElementRef) {}

  /**
   * After view init
   */
  ngAfterViewInit(): void {
    // Let the time to Js to render the element before hiding it
    setTimeout(() => {
      this.elementRef.nativeElement.style.display = this.hide ? 'none' : 'block';
    }, 100);
  }

  /**
   * On changes
   * Hide or show the element depending on the hide input
   * Not performed on the first change
   * @param changes
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['hide'].firstChange && changes['hide']) {
      this.elementRef.nativeElement.style.display = this.hide ? 'none' : 'block';
    }
  }
}
