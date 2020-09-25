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

import { Directive, ElementRef, Injector, OnInit } from '@angular/core';
import { LibraryService } from '../../dashboard/services/library.service';
import { Subject } from 'rxjs';

/**
 * Directive used for running script under HTML Views
 */
@Directive({
  selector: '[appRunScripts]'
})
export class RunScriptsDirective implements OnInit {
  /**
   * The constructor
   *
   * @param elementRef Represent a reference on an HTML Element
   * @param libraryService Frontend service used to manage the libraries
   */
  constructor(private readonly elementRef: ElementRef, private readonly libraryService: LibraryService) {}

  /**
   * Called when the directive is init
   */
  public ngOnInit(): void {
    setTimeout(() => this.reinsertScripts(), 0);
  }

  /**
   * Reinsert scripts tag inside DOM for execution
   */
  private reinsertScripts(): void {
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

      copyScript.onload = () => {
        this.scriptsLoadedCallback(this.libraryService);
      };
      copyScript.async = false;
      script.parentNode.replaceChild(copyScript, script);
    });
  }

  /**
   * Callback method called when all the required JS libraries are loaded
   *
   * @param libraryService The library service used to emit an event when the JS libraries are loaded
   */
  public scriptsLoadedCallback(libraryService: LibraryService): void {
    return libraryService.emitAreJSScriptsLoaded(true);
  }
}
