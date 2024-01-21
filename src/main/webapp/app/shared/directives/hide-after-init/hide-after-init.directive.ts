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