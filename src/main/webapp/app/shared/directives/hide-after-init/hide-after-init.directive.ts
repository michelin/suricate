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
    this.elementRef.nativeElement.style.display = this.hide ? 'none' : 'block';
  }

  /**
   * On changes
   * Hide or show the element depending on the hide input
   * Not performed on the first change
   * @param changes
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['hide'].previousValue != undefined && changes['hide']) {
      this.elementRef.nativeElement.style.display = this.hide ? 'none' : 'block';
    }
  }
}