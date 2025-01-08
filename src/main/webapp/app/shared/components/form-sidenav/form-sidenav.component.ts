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

import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { IconEnum } from '../../enums/icon.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { FormField } from '../../models/frontend/form/form-field';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';
import { FormSidenavConfiguration } from '../../models/frontend/sidenav/form-sidenav-configuration';
import { FormService } from '../../services/frontend/form/form.service';
import { SidenavService } from '../../services/frontend/sidenav/sidenav.service';

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
  public openFormSidenav: EventEmitter<void> = new EventEmitter<void>();

  /**
   * Send an event to the parent component used to close the sidebar
   */
  @Output()
  public closeFormSidenav: EventEmitter<void> = new EventEmitter<void>();

  /**
   * The configuration of the sidenav
   */
  public configuration: FormSidenavConfiguration;

  /**
   * The form displayed by the sidenav
   */
  public formGroup: UntypedFormGroup;

  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * The buttons
   */
  public buttons: ButtonConfiguration<unknown>[] = [];

  /**
   * Constructor
   *
   * @param formService Frontend service used to manage the forms
   * @param sidenavService Sidenav service used to manage the side navs
   */
  constructor(
    private readonly formService: FormService,
    private readonly sidenavService: SidenavService
  ) {
    this.initButtons();
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.sidenavService
      .listenFormSidenavMessages()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((configuration: FormSidenavConfiguration) => {
        this.configuration = configuration;
        this.formGroup = this.formService.generateFormGroupForFields(this.configuration.formFields);

        if (this.configuration?.slideToggleButtonConfiguration?.toggleChecked) {
          this.configuration.slideToggleButtonConfiguration.slideToggleButtonPressed(
            { source: undefined, checked: true },
            this.formGroup,
            this.configuration.formFields
          );
        }

        this.openSidenav();
      });
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.buttons.push(
      {
        label: 'close',
        icon: IconEnum.CLOSE,
        color: 'warn',
        callback: () => this.closeSidenav()
      },
      {
        label: 'save',
        icon: IconEnum.SAVE,
        color: 'primary',
        hidden: () => this.configuration.hideSaveAction,
        callback: () => this.save()
      }
    );
  }

  /**
   * Used to open the sidenav
   */
  private openSidenav(): void {
    this.openFormSidenav.emit();
  }

  /**
   * Used to close the sidenav
   */
  private closeSidenav(): void {
    this.closeFormSidenav.emit();
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.formService.validate(this.formGroup);

    if (this.formGroup.valid) {
      this.configuration.save(this.formGroup);
      this.closeSidenav();
    }
  }

  /**
   * Called when a value has changed
   *
   * @param valueChangedEvent The value changed
   */
  public valueChanged(valueChangedEvent: ValueChangedEvent): void {
    if (this.configuration.onValueChanged) {
      this.configuration
        .onValueChanged(valueChangedEvent)
        .pipe(takeUntil(this.unsubscribe))
        .subscribe((formFields: FormField[]) => {
          this.formGroup = this.formService.generateFormGroupForFields(formFields);
          this.configuration.formFields = formFields;
        });
    }
  }

  /**
   * Add the settings of the widget's category to the current widget settings form
   *
   * @param event The values retrieved from the child component event emitter
   */
  public getCategorySettings(event: MatSlideToggleChange): void {
    this.configuration.slideToggleButtonConfiguration.toggleChecked = event.checked;
    this.configuration.slideToggleButtonConfiguration.slideToggleButtonPressed(
      event,
      this.formGroup,
      this.configuration.formFields
    );
  }
}
