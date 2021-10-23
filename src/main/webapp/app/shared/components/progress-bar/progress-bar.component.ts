import { Component, Input } from '@angular/core';
import { ThemePalette } from '@angular/material/core';

@Component({
  selector: 'suricate-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss']
})
export class ProgressBarComponent {
  /**
   * The color to use with the spinner
   */
  @Input()
  public color: ThemePalette;

  /**
   * The value of the progress bar
   */
  @Input()
  public value: number;

  /**
   * Constructor
   */
  constructor() {}
}
