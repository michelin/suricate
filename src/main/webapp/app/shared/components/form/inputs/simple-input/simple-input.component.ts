import {Component, ViewEncapsulation} from '@angular/core';
import {InputComponent} from '../input.component';

/**
 * The default input component
 */
@Component({
  selector: 'app-simple-input',
  templateUrl: './simple-input.component.html',
  styleUrls: ['./simple-input.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SimpleInputComponent extends InputComponent {

  constructor() {
    super();
  }
}
