import { Directive, ElementRef, OnChanges, OnInit, SimpleChanges } from '@angular/core';

/**
 * Directive for Widget's JS scripts
 */
@Directive({ selector: '[widgetHtmlDirective]' })
export class WidgetHtmlDirective implements OnInit {
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
   * From all the JS scripts contained by the current widget HTML section, build new scripts then insert them in the DOM.
   * It executes the scripts again and render the widget properly.
   * This is called once the HTML of the widget is fully loaded.
   */
  private reloadJSScripts() {
    const scripts: HTMLScriptElement[] = (<HTMLScriptElement[]>(
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
