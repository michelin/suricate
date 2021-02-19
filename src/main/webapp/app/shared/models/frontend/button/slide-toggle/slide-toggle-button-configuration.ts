import { FormField } from '../../form/form-field';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { FormGroup } from '@angular/forms';

export class SlideToggleButtonConfiguration {
  /**
   * Display or hide the slide toggle button
   */
  displaySlideToggleButton?: boolean;

  /**
   * Check the slide toggle button
   */
  toggleChecked?: boolean;

  /**
   * Function called when the slide toggle button is pressed
   */
  slideToggleButtonPressed?: (event: MatSlideToggleChange, formGroup: FormGroup, formField: FormField[]) => void;
}
