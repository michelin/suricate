import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';

/**
 * Manage the instantiation of different form inputs
 */
@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss']
})
export class InputComponent {

  /**
   * The form created in which we have to create the input
   */
  @Input()
  form: FormGroup;

  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  field: any;

  /**
   * True if the field should be readonly
   */
  @Input()
  isReadOnly: boolean;

  /**
   * An identifier for the field
   */
  @Input()
  identifier: any;

  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  valueChangeEvent = new EventEmitter<any>();

  constructor() {
  }

  /**
   * Retrieve the form control from the form
   */
  getFormControl() {
    return this.form.get(this.field.key);
  }

  /**
   * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
   *
   * @param id The id of the field
   * @param value The new value
   */
  emitValueChange(id, value) {
    this.valueChangeEvent.emit({identifier: id, value: value});
  }

  /**
   * Tell if it's a required field
   */
  isRequired() {
    return !this.isReadOnly && this.field && this.field.validators && this.field.validators.includes(Validators.required);
  }
}
