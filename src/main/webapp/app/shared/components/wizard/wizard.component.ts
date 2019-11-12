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

import { Component, Injector, OnInit, ViewChild } from '@angular/core';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { FormGroup } from '@angular/forms';
import { WizardConfiguration } from '../../models/frontend/wizard/wizard-configuration';
import { FormService } from '../../services/frontend/form.service';
import { FormStep } from '../../models/frontend/form/form-step';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { MatStepper } from '@angular/material';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { ActivatedRoute } from '@angular/router';

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
   * Frontend service used to help on the form creation
   */
  private readonly formService: FormService;
  /**
   * @param activatedRoute Angular service used to manage the route activated by the current component
   */
  protected readonly activatedRoute: ActivatedRoute;

  /**
   * The configuration of the header
   */
  protected headerConfiguration = new HeaderConfiguration();
  /**
   * The configuration of the wizard
   */
  public wizardConfiguration: WizardConfiguration;
  /**
   * The list of wizard buttons
   */
  public wizardButtons: ButtonConfiguration<unknown>[];
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
   *
   * @param injector Angular service used to manage injection of service
   */
  constructor(protected readonly injector: Injector) {
    this.formService = injector.get(FormService);
    this.activatedRoute = injector.get(ActivatedRoute);

    this.initWizardButtons();
  }

  /**
   * Init the buttons of the wizard
   */
  initWizardButtons(): void {
    this.wizardButtons = [
      {
        label: 'Close',
        color: 'warn'
      },
      {
        label: 'Back',
        color: 'primary',
        hidden: () => this.shouldDisplayBackButton(),
        callback: () => this.backAction()
      },
      {
        label: 'Next',
        color: 'primary',
        hidden: () => this.shouldDisplayNextButton(),
        callback: () => this.nextAction()
      },
      {
        label: 'Done',
        color: 'primary',
        hidden: () => this.shouldDisplayDoneButton()
      }
    ];
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
