import {Component} from '@angular/core';
import {InputComponent} from '../input.component';

/**
 * Manage the instantiation of the checkbox
 */
@Component({
  selector: 'app-checkbox',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.scss']
})
export class CheckboxComponent extends InputComponent {

  constructor() {
    super();
  }

}
