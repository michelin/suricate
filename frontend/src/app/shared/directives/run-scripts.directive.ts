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
   *
   * @type {EventEmitter<any>}
   */
  @Output('scriptRenderingFinished') scriptRenderingFinished = new EventEmitter();

  /**
   * The constructor
   *
   * @param {ElementRef} elementRef Represent a reference for an HTML Element
   */
  constructor(private elementRef: ElementRef) { }

  /**
   * Execute when the directive is init
   */
  ngOnInit(): void {
    setTimeout(() => {
      // Wait for DOM rendering
      this.reinsertScripts();
    });
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
    }, 100);
  }
}
