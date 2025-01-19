/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { animate, style, transition, trigger } from '@angular/animations';
import { NgClass } from '@angular/common';
import { Component, ElementRef, inject, Input, OnInit, ViewEncapsulation } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormsModule,
  ReactiveFormsModule,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/core';
import { MatError, MatFormField, MatHint, MatLabel, MatPrefix, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { ButtonColorEnum } from '../../../enums/button-color.enum';
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { IconEnum } from '../../../enums/icon.enum';
import { ButtonConfiguration } from '../../../models/frontend/button/button-configuration';
import { FormField } from '../../../models/frontend/form/form-field';
import { FormOption } from '../../../models/frontend/form/form-option';
import { ValueChangedEvent, ValueChangedType } from '../../../models/frontend/form/value-changed-event';
import { ButtonsComponent } from '../../buttons/buttons.component';
import { BaseInputComponent } from '../base-input/base-input/base-input.component';
import { CheckboxComponent } from '../checkbox/checkbox.component';
import { ColorPickerComponent } from '../color-picker/color-picker.component';
import { FileInputComponent } from '../file-input/file-input.component';
import { MosaicComponent } from '../mosaic/mosaic.component';

/**
 * Manage the instantiation of different form inputs
 */
@Component({
  selector: 'suricate-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss'],
  encapsulation: ViewEncapsulation.None,
  animations: [
    trigger('animationError', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-100%)' }),
        animate('300ms cubic-bezier(0.55, 0, 0.55, 0.2)', style({ opacity: 1, transform: 'translateY(0%)' }))
      ])
    ])
  ],
  imports: [
    MatFormField,
    NgClass,
    MatIcon,
    MatPrefix,
    MatTooltip,
    MatLabel,
    MatInput,
    FormsModule,
    MatAutocompleteTrigger,
    ReactiveFormsModule,
    MatAutocomplete,
    MatOption,
    MatSelect,
    MatSuffix,
    MatHint,
    MatError,
    CheckboxComponent,
    FileInputComponent,
    ColorPickerComponent,
    MosaicComponent,
    TranslatePipe,
    ButtonsComponent
  ]
})
export class InputComponent extends BaseInputComponent implements OnInit {
  /**
   * A reference to a component. Used to take screenshot
   */
  @Input()
  public componentRef: ElementRef;

  /**
   * Translate service
   */
  protected translateService: TranslateService;

  /**
   * The data type enum
   */
  public dataType = DataTypeEnum;

  /**
   * The list of options to display
   */
  public options: FormOption[];

  /**
   * Is the current field a password or not
   */
  public originalTypeIsPassword: boolean;

  /**
   * Configuration for the delete row button
   */
  public deleteRowButtonConfiguration: ButtonConfiguration<{ formGroup: UntypedFormGroup; index: number }>[];

  /**
   * Constructor
   */
  constructor() {
    super();
    this.translateService = inject(TranslateService);
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.originalTypeIsPassword = this.field.type === DataTypeEnum.PASSWORD;

    this.initOptionsField();

    if (this.field.deleteRow) {
      this.initDeleteRowConfiguration();
    }
  }

  /**
   * Init the field options
   */
  private initOptionsField(): void {
    if (this.field.options) {
      this.field.options().subscribe((options: FormOption[]) => {
        this.options = options;
      });
    }
  }

  /**
   * Retrieve the form control from the form as a form array
   */
  public getFormControlAsFormArray(): AbstractControl[] {
    return (this.formGroup.controls[this.field.key] as FormArray).controls;
  }

  /**
   * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
   */
  public override emitValueChangeEventFromType(type: ValueChangedType): void {
    this.manageAutoCompleteChanges();
    super.emitValueChangeEventFromType(type);
  }

  /**
   * Emit a value change event when the value of a child input component has changed
   * @param event The value change event
   */
  public emitValueChangeEvent(event: ValueChangedEvent): void {
    this.valueChangeEvent.emit(event);
  }

  /**
   * Refresh the list to display in auto complete
   */
  private manageAutoCompleteChanges(): void {
    if (this.field.options && this.field.type === DataTypeEnum.TEXT) {
      const inputValue = this.formGroup.value[this.field.key];

      this.field.options(inputValue).subscribe((options) => {
        this.options = options;
      });
    }
  }

  /**
   * Tell if it's a required field
   */
  public isRequired(): boolean {
    let isRequired = false;

    if (this.field && this.field.validators && this.field.validators && !this.field.readOnly) {
      isRequired = Array.isArray(this.field.validators)
        ? this.field.validators.includes(Validators.required)
        : this.field.validators === Validators.required;
    }

    return isRequired;
  }

  /**
   * Execute an action when clicking on the suffix icon depending on the type of
   * the field
   */
  public suffixActions(): void {
    if (this.originalTypeIsPassword) {
      if (this.field.type === DataTypeEnum.PASSWORD) {
        this.field.type = DataTypeEnum.TEXT;
        this.field.iconSuffix = IconEnum.HIDE_PASSWORD;
      } else {
        this.field.type = DataTypeEnum.PASSWORD;
        this.field.iconSuffix = IconEnum.SHOW_PASSWORD;
      }
    }
  }

  /**
   * Calculate the size of a cell in a row
   */
  public getInnerFormSize(): number {
    let cellSize = 87;

    if (this.field.fields && this.field.fields.length > 0) {
      const numberOfFieldDisplayed = this.field.fields.filter((field: FormField) => field.type !== DataTypeEnum.HIDDEN);
      cellSize = 87 / numberOfFieldDisplayed.length;
    }

    return cellSize;
  }

  /**
   * Delete a row
   *
   * @param innerFormGroup The form group that reflect the row
   * @param index The index of the row in the parent form
   */
  public deleteRow(innerFormGroup: UntypedFormGroup, index: number): void {
    this.field.deleteRow.callback(innerFormGroup.value[this.field.deleteRow.attribute]).subscribe(() => {
      (this.formGroup.controls[this.field.key] as FormArray).removeAt(index);
    });
  }

  /**
   * Init delete row configuration
   */
  private initDeleteRowConfiguration(): void {
    this.deleteRowButtonConfiguration = [
      {
        icon: IconEnum.DELETE,
        color: ButtonColorEnum.WARN,
        variant: 'miniFab',
        callback: (event: Event, object: { formGroup: UntypedFormGroup; index: number }) => {
          this.deleteRow(object.formGroup, object.index);
        }
      }
    ];
  }

  /**
   * Is the current field an HTML input
   */
  public isHtmlInput(): boolean {
    return (
      this.field.type === DataTypeEnum.NUMBER ||
      this.field.type === DataTypeEnum.TEXT ||
      this.field.type === DataTypeEnum.TEXTAREA ||
      this.field.type === DataTypeEnum.PASSWORD ||
      this.field.type === DataTypeEnum.COMBO ||
      this.field.type === DataTypeEnum.MULTIPLE
    );
  }

  /**
   * Is the current field a simple input
   */
  public isSimpleInput(): boolean {
    return (
      this.field.type === DataTypeEnum.NUMBER ||
      this.field.type === DataTypeEnum.TEXT ||
      this.field.type === DataTypeEnum.PASSWORD
    );
  }

  /**
   * Is the current field a select input
   */
  public isSelectInput(): boolean {
    return this.field.type === DataTypeEnum.COMBO || this.field.type === DataTypeEnum.MULTIPLE;
  }
}
