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
import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import html2canvas from 'html2canvas';

import { FileUtils } from '../../../utils/file.utils';
import { BaseInputComponent } from '../base-input/base-input/base-input.component';

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
  imports: [MatButton, NgClass, MatIcon, MatError, TranslatePipe]
})
export class FileInputComponent extends BaseInputComponent implements OnInit {
  /**
   * A reference to a component. Used to take screenshot
   */
  @Input()
  public componentRef: ElementRef;

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

      this.emitValueChangeEventFromType('fileChanged');
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

      this.emitValueChangeEventFromType('fileChanged');
    });
  }
}
