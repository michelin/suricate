import { Directive, ElementRef, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProjectWidget } from '../models/backend/project-widget/project-widget';

/**
 * Directive for Widget's JS scripts
 */
@Directive({ selector: '[widgetHtmlDirective]' })
export class WidgetHtmlDirective implements OnChanges {
  /**
   * The rendered project widget
   */
  @Input()
  public projectWidget: ProjectWidget;

  /**
   * Constructor
   *
   * @param elementRef The reference to the element where the directive is set
   */
  constructor(private readonly elementRef: ElementRef) {}

  /**
   * On changes
   *
   * @param changes The change event
   */
  ngOnChanges(changes: SimpleChanges): void {
    // When the widget changes, reapply the JS scripts
    if (changes.projectWidget) {
      this.reapplyJSScripts();
    }
  }

  /**
   * From all the JS scripts contained by the current widget HTML section, build new scripts then insert them in the DOM.
   * It executes the scripts again and render the widget properly.
   * This is called once the HTML of the widget is fully loaded.
   */
  private reapplyJSScripts() {
    const scripts: HTMLScriptElement[] = (<HTMLScriptElement[]>(
      Array.from(this.elementRef.nativeElement.getElementsByTagName('script'))
    )).filter(currentScript => currentScript.innerHTML);

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
