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

import { Component, OnInit, ViewChild } from '@angular/core';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { FormGroup } from '@angular/forms';
import { WizardConfiguration } from '../../models/frontend/wizard/wizard-configuration';
import { FormService } from '../../services/frontend/form.service';
import { FormStep } from '../../models/frontend/form/form-step';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { MatStepper } from '@angular/material';

/**
 * Generic component used to display wizards
 */
@Component({
  template: '',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements OnInit {
  /**
   * Reference on the stepper
   */
  @ViewChild('wizardStepper', { static: true })
  public wizardStepper: MatStepper;

  /**
   * The configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The configuration of the wizard
   */
  public wizardConfiguration: WizardConfiguration;

  /**
   * Form group of the stepper
   */
  public stepperFormGroup: FormGroup;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   */
  constructor(private readonly formService: FormService) {
    this.configureHeader();
  }

  /**
   * Configure the header component
   */
  configureHeader(): void {
    this.headerConfiguration = { title: 'Generic wizard' };
  }

  ngOnInit() {
    this.stepperFormGroup = this.formService.generateFormGroupForSteps(this.wizardConfiguration.steps);
  }

  /**
   * Used to know if the back button should be displayed
   */
  private shouldDisplayBackButton(): boolean {
    return this.wizardStepper && this.wizardStepper.selectedIndex > 0;
  }

  /**
   * Used to know if the back button should be displayed
   */
  private shouldDisplayNextButton(): boolean {
    return this.wizardStepper && this.wizardStepper.steps && this.wizardStepper.selectedIndex < this.wizardStepper.steps.length - 1;
  }

  /**
   * Used to know if the back button should be displayed
   */
  private shouldDisplayDoneButton(): boolean {
    return this.wizardStepper && this.wizardStepper.steps && this.wizardStepper.selectedIndex === this.wizardStepper.steps.length - 1;
  }

  /**
   * Execute back action on stepper
   */
  private backAction(): void {
    this.wizardStepper.previous();
  }

  /**
   * Execute next action on stepper
   */
  private nextAction(): void {
    this.wizardStepper.next();
  }

  /**
   * Retrieve a form group of a step
   *
   * @param step The step
   */
  protected getFormGroupOfStep(step: FormStep): FormGroup {
    return this.stepperFormGroup.controls[step.key] as FormGroup;
  }
}
