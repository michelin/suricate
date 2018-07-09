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


import {Directive, ElementRef, EventEmitter, OnInit, Output} from '@angular/core';

/**
 * Directive used for running script under HTML Views
 */
@Directive({
  selector: '[appRunScripts]'
})
export class RunScriptsDirective implements OnInit {

  /**
   * Emit an event when the script rendering for childs are ended
   * @type {EventEmitter<any>}
   */
  @Output('scriptRenderingFinished') scriptRenderingFinished = new EventEmitter();

  /**
   * The constructor
   *
   * @param {ElementRef} elementRef Represent a reference for an HTML Element
   */
  constructor(private elementRef: ElementRef) {
  }

  /**
   * Execute when the directive is init
   */
  ngOnInit(): void {
    setTimeout(() => {
      // Wait for DOM rendering
      this.reinsertScripts();
    }, 0);
  }

  /**
   * Reinsert scripts tag inside DOM for execution
   */
  reinsertScripts(): void {
    let scripts: HTMLScriptElement[] = Array.from(this.elementRef.nativeElement.getElementsByTagName('script'));
    const scriptsWithSrc: HTMLScriptElement[] = scripts.filter(currentScript => currentScript.src);
    const scriptsInline: HTMLScriptElement[] = scripts.filter(currentScript => currentScript.innerHTML);
    scripts = [...scriptsWithSrc, ...scriptsInline];

    const scriptsInitialLength = scripts.length;

    Array.from(Array(scriptsInitialLength).keys()).forEach((index: number) => {
      const script = scripts[index];

      // Create a new script tag inside DOM
      const copyScript: HTMLScriptElement = document.createElement('script');
      copyScript.type = script.type ? script.type : 'text/javascript';

      // Check if this is a inline script or a reference to an external script
      if (script.innerHTML) {
        copyScript.innerHTML = script.innerHTML;
      } else if (script.src) {
        copyScript.src = script.src;
      }

      copyScript.async = false;
      script.parentNode.replaceChild(copyScript, script);
    });

    // Wait for DOM rendering
    setTimeout(() => {
      this.scriptRenderingFinished.emit(true);
    }, 500);
  }
}
