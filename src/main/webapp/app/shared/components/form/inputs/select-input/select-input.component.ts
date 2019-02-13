import {Component} from '@angular/core';
import {InputComponent} from '../input.component';
import {MatOption} from '@angular/material';

/**
 * Manage every types select input
 */
@Component({
  selector: 'app-select-input',
  templateUrl: './select-input.component.html',
  styleUrls: ['./select-input.component.scss']
})
export class SelectInputComponent extends InputComponent {

  constructor() {
    MatOption;
    super();
  }
}
