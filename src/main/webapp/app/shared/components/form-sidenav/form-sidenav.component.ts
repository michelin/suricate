/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormService } from '../../services/frontend/form.service';
import { SidenavService } from '../../services/frontend/sidenav.service';
import { FormSidenavConfiguration } from '../../models/frontend/sidenav/form-sidenav-configuration';
import { FormGroup } from '@angular/forms';
import { takeWhile } from 'rxjs/operators';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { IconEnum } from '../../enums/icon.enum';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';
import { FormField } from '../../models/frontend/form/form-field';

/**
 * Component used to display the form sidenav
 */
@Component({
  selector: 'suricate-form-sidenav',
  templateUrl: './form-sidenav.component.html',
  styleUrls: ['./form-sidenav.component.scss']
})
export class FormSidenavComponent implements OnInit, OnDestroy {
  /**
   * Send an event to the parent component used to open the sidebar
   */
  @Output()
  public open = new EventEmitter<void>();
  /**
   * Send an event to the parent component used to close the sidebar
   */
  @Output()
  public close = new EventEmitter<void>();

  /**
   * The configuration of the sidenav
   */
  private configuration: FormSidenavConfiguration;

  /**
   * The form displayed by the sidenav
   */
  private formGroup: FormGroup;

  /**
   * Used to unsubscribe observables when the component is destroyed
   */
  private isAlive = true;

  /**
   * The buttons
   */
  public buttons: ButtonConfiguration<unknown>[] = [];

  /**
   * Constructor
   *
   * @param formService Frontend service used to manage the forms
   * @param sidenavService Sidenav service used to manage the sidenavs
   */
  constructor(private readonly formService: FormService, private readonly sidenavService: SidenavService) {
    this.initButtons();
  }

  /**
   * Called when the component is init
   */
  ngOnInit(): void {
    this.sidenavService
      .listenFormSidenavMessages()
      .pipe(takeWhile(() => this.isAlive))
      .subscribe((configuration: FormSidenavConfiguration) => {
        this.configuration = configuration;
        this.formGroup = this.formService.generateFormGroupForFields(this.configuration.formFields);
        this.openSidenav();
      });
  }

  /**
   * Init the buttons
   */
  initButtons() {
    this.buttons.push(
      {
        label: 'Close',
        icon: IconEnum.CLOSE,
        color: 'warn',
        callback: () => this.closeSidenav()
      },
      {
        label: 'Save',
        icon: IconEnum.SAVE,
        color: 'primary',
        callback: () => this.save()
      }
    );
  }

  /**
   * Used to open the sidenav
   */
  private openSidenav(): void {
    this.open.emit();
  }

  /**
   * Used to close the sidenav
   */
  private closeSidenav(): void {
    this.close.emit();
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.configuration.save();
    this.closeSidenav();
  }

  /**
   * Called when a value has changed
   *
   * @param valueChangedEvent The value changed
   */
  public valueChanged(valueChangedEvent: ValueChangedEvent): void {
    if (this.configuration.onValueChanged) {
      this.configuration.onValueChanged(valueChangedEvent).subscribe((formFields: FormField[]) => {
        this.formGroup = this.formService.generateFormGroupForFields(formFields);
        this.configuration.formFields = formFields;
      });
    }
  }

  /**
   * Called when the component is destroyed
   */
  ngOnDestroy(): void {
    this.isAlive = false;
  }
}
