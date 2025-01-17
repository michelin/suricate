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
import { Component, ElementRef, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import html2canvas from 'html2canvas';

import { FileUtils } from '../../../utils/file.utils';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { FormField } from '../../../models/frontend/form/form-field';
import { ValueChangedEvent, ValueChangedType } from '../../../models/frontend/form/value-changed-event';
import { MaterialIconRecords } from '../../../records/material-icon.record';
import { IconEnum } from '../../../enums/icon.enum';

/**
 * Component that manage the file input
 */
@Component({
  selector: 'suricate-file-input',
  templateUrl: './file-input.component.html',
  styleUrls: ['./file-input.component.scss'],
  animations: [
    trigger('animationError', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-100%)' }),
        animate('300ms cubic-bezier(0.55, 0, 0.55, 0.2)', style({ opacity: 1, transform: 'translateY(0%)' }))
      ])
    ])
  ],
  standalone: true,
  imports: [MatButton, NgClass, MatIcon, MatError, TranslatePipe]
})
export class FileInputComponent implements OnInit {
  /**
   * Object that hold different information used for the instantiation of the input
   */
  @Input()
  public field: FormField;

  /**
   * The form created in which we have to create the input
   */
  @Input()
  public formGroup: UntypedFormGroup;

  /**
   * Event sent when the value of the input has changed
   */
  @Output()
  public valueChangeEvent = new EventEmitter<ValueChangedEvent>();

  /**
   * A reference to a component. Used to take screenshot
   */
  @Input()
  public componentRef: ElementRef;

  /**
   * The list of material icon codes
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The image as base 64
   */
  public imgBase64: string | ArrayBuffer;

  /**
   * If it's not an image we set the filename
   */
  public filename: string;

  /**
   * When the component is init
   */
  public ngOnInit(): void {
    this.setBase64File(this.field.value as string);
  }

  /**
   * Use to display image or filename
   *
   * @param base64Url The base64 url of the image
   * @param filename The filename if the file is not an image
   */
  private setBase64File(base64Url?: string, filename?: string): void {
    if (FileUtils.isBase64UrlIsAnImage(base64Url)) {
      this.imgBase64 = base64Url;
      this.filename = null;
    } else {
      this.imgBase64 = null;
      this.filename = filename;
    }
  }

  /**
   * Manage the change event return by the input
   *
   * @param event The file change event
   */
  public onFileChange(event: Event): void {
    this.convertFileBase64(event);
  }

  /**
   * Convert a file into base64
   *
   * @param event The change event
   */
  public convertFileBase64(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    if (!inputElement.files || inputElement.files.length === 0) {
      return;
    }

    const file: File = inputElement.files[0];
    FileUtils.convertFileToBase64(file).subscribe((base64Url: string | ArrayBuffer) => {
      const base64String = base64Url as string;
      const fileName = file.name;

      this.setBase64File(base64String, fileName);
      this.getFormControl().setValue(base64String);
      this.getFormControl().markAsDirty();
      this.getFormControl().markAsTouched();

      this.emitValueChange('fileChanged');
    });
  }

  /**
   * Take a screenshot of the dashboard
   */
  public screenshot(): void {
    html2canvas(this.componentRef.nativeElement, {
      backgroundColor: 'transparent',
      foreignObjectRendering: true,
      scrollX: -190,
      scrollY: -100
    }).then((htmlCanvasElement: HTMLCanvasElement) => {
      const b64: string = htmlCanvasElement.toDataURL('image/png');
      this.setBase64File(b64);
      this.getFormControl().setValue(b64);
      this.getFormControl().markAsDirty();
      this.getFormControl().markAsTouched();

      this.emitValueChange('fileChanged');
    });
  }

  /**
   * Function called when a field has been changed in the form, emit and event that will be caught by the parent component
   */
  public emitValueChange(type: ValueChangedType): void {
    this.valueChangeEvent.emit({
      fieldKey: this.field.key,
      value: this.formGroup.value[this.field.key],
      type: type // TODO: Check if this is still needed
    });
  }

  /**
   * Retrieve the form control from the form
   */
  public getFormControl(): AbstractControl | null {
    return this.formGroup.controls[this.field.key];
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
    if (this.getFormControl()['errors']?.['pattern']) {
      return 'field.error.pattern';
    }

    return undefined;
  }
}
