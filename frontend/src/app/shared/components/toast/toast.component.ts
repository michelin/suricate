/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {ToastMessage} from '../../model/toastNotification/ToastMessage';
import {Observable} from 'rxjs/Observable';
import {ToastService} from './toast.service';
import {animate, group, state, style, transition, trigger} from '@angular/animations';
import {ToastType} from '../../model/toastNotification/ToastType';
import {takeWhile} from 'rxjs/operators';

/**
 * Component that display toast notification messages
 */
@Component({
  selector: 'app-toast-messages',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        'max-height': '500px', 'opacity': '1', 'visibility': 'visible'
      })),
      state('out', style({
        'max-height': '0px', 'opacity': '0', 'visibility': 'hidden'
      })),
      transition('in => out', [group([
            animate('400ms ease-in-out', style({
              'opacity': '0'
            })),
            animate('600ms ease-in-out', style({
              'max-height': '0px'
            })),
            animate('700ms ease-in-out', style({
              'visibility': 'hidden'
            }))
          ]
      )]),
      transition('out => in', [group([
            animate('1ms ease-in-out', style({
              'visibility': 'visible'
            })),
            animate('600ms ease-in-out', style({
              'max-height': '500px'
            })),
            animate('800ms ease-in-out', style({
              'opacity': '1'
            }))
          ]
      )])
    ])
  ]
})
export class ToastComponent implements OnInit, OnDestroy {

  /**
   * Used for keep the subscription of subjects/Observables open
   * @type {boolean}
   */
  private _isAlive = true;

  /**
   * The component state
   * @type {string}
   */
  animationState = 'in';

  /**
   * The enums of toast type
   * @type {ToastType}
   */
  ToastType = ToastType;

  /**
   * The message to display
   * @type {Observable<ToastMessage>}
   */
  message$: Observable<ToastMessage>;

  /**
   * The current timer for @function {hideWithinTimeout} function
   * @type {NodeJS.Timer}
   */
  hideTimer: NodeJS.Timer;

  /**
   * Constructor
   *
   * @param {ToastService} _toastService The toast service to inject
   */
  constructor(private _toastService: ToastService) {
  }

  /**
   * Called when the component is init
   */
  ngOnInit() {
    this.message$ = this._toastService.toastMessage$;
    this.message$
        .pipe(
            takeWhile(() => this._isAlive)
        )
        .subscribe(() => this.showToast());
  }

  /**
   * Show the toast notification
   */
  showToast() {
    this.clearTimeout();
    this.animationState = 'in';
    this.hideWithinTimeout();
  }

  /**
   * Hide manually the toast notification
   */
  hideToast() {
    this.clearTimeout();
    this.animationState = 'out';
  }

  /**
   * Hide the toast notification with timer
   */
  hideWithinTimeout() {
    this.hideTimer = setTimeout(() => this.hideToast(), 4000);
  }

  /**
   * Clear the timer
   */
  clearTimeout() {
    clearTimeout(this.hideTimer);
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy() {
    this._isAlive = false;
  }

}
