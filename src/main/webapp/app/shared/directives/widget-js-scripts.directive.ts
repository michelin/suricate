import { Directive, ElementRef, OnInit } from '@angular/core';

/**
 * Directive for Widget's JS scripts
 */
@Directive({ selector: '[suricateWidgetJsScripts]' })
export class WidgetJsScriptsDirective implements OnInit {
  /**
   * Constructor
   *
   * @param elementRef The reference to the element where the directive is set
   */
  constructor(private readonly elementRef: ElementRef) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.reloadJSScripts();
  }

  /**
   * From all the JS scripts contained by the current widget, build new scripts then insert them in the DOM.
   * It executes the scripts again and render the widget properly.
   */
  private reloadJSScripts() {
    let scripts: HTMLScriptElement[] = (<HTMLScriptElement[]>(
      Array.from(this.elementRef.nativeElement.getElementsByTagName('script'))
    )).filter(currentScript => currentScript.src || currentScript.innerHTML);

    Array.from(Array(scripts.length).keys()).forEach((index: number) => {
      const script = scripts[index];

      const copyScript: HTMLScriptElement = document.createElement('script');
      copyScript.type = script.type ? script.type : 'text/javascript';
      copyScript.innerHTML = script.innerHTML;
      copyScript.async = false;
      script.parentNode.replaceChild(copyScript, script);
    });
  }
}
