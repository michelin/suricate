import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';

import { IconEnum } from '../../../../enums/icon.enum';
import { FormField } from '../../../../models/frontend/form/form-field';
import { ValueChangedEvent, ValueChangedType } from '../../../../models/frontend/form/value-changed-event';
import { MaterialIconRecords } from '../../../../records/material-icon.record';

@Component({
	selector: 'suricate-base-input',
	imports: [],
	template: ''
})
export class BaseInputComponent {
	/**
	 * The form field
	 */
	@Input()
	public field: FormField;

	/**
	 * The form group
	 */
	@Input()
	public formGroup: UntypedFormGroup;

	/**
	 * Event sent when the value of the input has changed
	 */
	@Output()
	public valueChangeEvent = new EventEmitter<ValueChangedEvent>();

	/**
	 * The list of icons
	 */
	public iconEnum = IconEnum;

	/**
	 * The list of material icon codes
	 */
	public materialIconRecords = MaterialIconRecords;

	/**
	 * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
	 */
	public emitValueChangeEventFromType(type: ValueChangedType): void {
		this.valueChangeEvent.emit({
			fieldKey: this.field.key,
			value: this.formGroup.value[this.field.key],
			type: type
		});
	}

	/**
	 * Test if the field is on error
	 */
	public isInputFieldOnError(): boolean {
		return (this.getFormControl().dirty || this.getFormControl().touched) && this.getFormControl().invalid;
	}

	/**
	 * Get the first error triggered by the current field.
	 * Return the string code of the error to display it.
	 */
	public getInputErrors(): string {
		if (this.getFormControl()['errors']?.['required']) {
			return 'field.error.required';
		}

		if (this.getFormControl()['errors']?.['minlength']) {
			return 'field.error.length';
		}

		if (this.getFormControl()['errors']?.['email']) {
			return 'field.error.email.format';
		}

		if (this.getFormControl()['errors']?.['passwordMismatch']) {
			return 'field.error.password.mismatch';
		}

		if (this.getFormControl()['errors']?.['pattern']) {
			return 'field.error.pattern';
		}

		if (this.getFormControl()['errors']?.['digits']) {
			return 'field.error.digits';
		}

		if (this.getFormControl()['errors']?.['gt0']) {
			return 'field.error.gt0';
		}

		if (this.getFormControl()['errors']?.['uniquePriority']) {
			return 'field.error.repository.unique.priority';
		}

		return undefined;
	}

	/**
	 * Retrieve the form control from the form
	 */
	public getFormControl(): AbstractControl | null {
		return this.formGroup.controls[this.field.key];
	}
}
