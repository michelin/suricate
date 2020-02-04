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

import { Component, Injector, Input, OnChanges, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { FormGroup } from '@angular/forms';
import { WizardConfiguration } from '../../models/frontend/wizard/wizard-configuration';
import { FormService } from '../../services/frontend/form.service';
import { FormStep } from '../../models/frontend/form/form-step';
import { MaterialIconRecords } from '../../records/material-icon.record';
import { MatStepper } from '@angular/material';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { ActivatedRoute, Router } from '@angular/router';
import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';
import { FormField } from '../../models/frontend/form/form-field';
import { takeWhile } from 'rxjs/operators';
import { ProjectWidgetFormStepsService } from '../../form-steps/project-widget-form-steps.service';
import { HttpCategoryService } from '../../services/backend/http-category.service';
import { WidgetConfiguration } from '../../models/backend/widget-configuration/widget-configuration';
import { WidgetConfigurationFormFieldsService } from '../../form-fields/widget-configuration-form-fields.service';
import { DataTypeEnum } from '../../enums/data-type.enum';
import ChangeEvent = JQuery.ChangeEvent;
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

/**
 * Generic component used to display wizards
 */
@Component({
  template: '',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements OnInit, OnDestroy {
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
   * Frontend service used to retrieve category information
   */
  private readonly categoryService: HttpCategoryService;
  /**
   * Frontend service used to help on the widget configuration form fields creation
   */
  private readonly widgetConfigurationFormFieldsService: WidgetConfigurationFormFieldsService;
  /**
   * Angular service used to manage the route activated by the current component
   */
  protected readonly activatedRoute: ActivatedRoute;
  /**
   * Angular service used to manage application routes
   */
  protected readonly router: Router;

  /**
   * Used to know if the component is instantiated
   */
  private isAlive = true;
  /**
   * The configuration of the header
   */
  public headerConfiguration = new HeaderConfiguration();
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
  private stepperFormGroup: FormGroup;
  /**
   * The list of material icons
   */
  protected materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param injector Angular service used to manage injection of service
   */
  constructor(protected readonly injector: Injector) {
    this.formService = injector.get(FormService);
    this.widgetConfigurationFormFieldsService = injector.get(WidgetConfigurationFormFieldsService);
    this.categoryService = injector.get(HttpCategoryService);
    this.activatedRoute = injector.get(ActivatedRoute);
    this.router = injector.get(Router);

    this.initWizardButtons();
  }

  /**
   * Init the buttons of the wizard
   */
  private initWizardButtons(): void {
    this.wizardButtons = [
      {
        label: 'close',
        color: 'warn',
        callback: () => this.closeWizard()
      },
      {
        label: 'back',
        color: 'primary',
        hidden: () => !this.shouldDisplayBackButton(),
        callback: () => this.backAction()
      },
      {
        label: 'next',
        color: 'primary',
        hidden: () => !this.shouldDisplayNextButton(),
        callback: () => this.nextAction()
      },
      {
        label: 'done',
        color: 'primary',
        hidden: () => !this.shouldDisplayDoneButton(),
        callback: () => this.saveWizard(this.stepperFormGroup.value)
      }
    ];
  }

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.stepperFormGroup = this.formService.generateFormGroupForSteps(this.wizardConfiguration.steps);
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.isAlive = false;
  }

  /**
   * If we have async fields on the new step we load them
   *
   * @param stepperSelectionEvent The step change event
   */
  public onStepChanged(stepperSelectionEvent: StepperSelectionEvent): void {
    const currentStep = this.wizardConfiguration.steps[stepperSelectionEvent.selectedIndex];

    if (currentStep && currentStep.asyncFields) {
      currentStep
        .asyncFields((stepperSelectionEvent.selectedStep.stepControl as unknown) as FormGroup, currentStep)
        .pipe(takeWhile(() => this.isAlive))
        .subscribe((formFields: FormField[]) => {
          currentStep.fields = formFields;
          this.stepperFormGroup.setControl(currentStep.key, this.formService.generateFormGroupForFields(formFields));
        });
    }
  }

  /**
   * Called when a value of the form has changed
   *
   * @param valueChangeEvent The value change event
   */
  protected onValueChanged(valueChangeEvent: ValueChangedEvent): void {
    if (valueChangeEvent.type === 'mosaicOptionSelected' && !this.shouldDisplayDoneButton()) {
      setTimeout(() => this.wizardStepper.next(), 500);
    }
  }

  /**
   * Get the category settings. Then, for each category settings, add a field and a control in the widget settings form.
   *
   * @param event the slide toggle change event
   * @param step The step containing the widget and the category information from which to retrieve the category settings
   */
  protected getCategorySettings(event: MatSlideToggleChange, step: FormStep): void {
    this.categoryService.getCategoryConfigurations(step.category.id).subscribe(value => {
      const categorySettingsFormFields = this.widgetConfigurationFormFieldsService.generateWidgetConfigurationFormFields(value);

      if (event.checked) {
        step.fields.push(...categorySettingsFormFields);
        this.formService.addControlsToFormGroupForFields(
          this.stepperFormGroup.controls[ProjectWidgetFormStepsService.configureWidgetStepKey] as FormGroup,
          categorySettingsFormFields
        );
      } else {
        step.fields = step.fields.filter(field => categorySettingsFormFields.findIndex(setting => setting.key === field.key) === -1);
        this.formService.removeControlsToFormGroupForFields(
          this.stepperFormGroup.controls[ProjectWidgetFormStepsService.configureWidgetStepKey] as FormGroup,
          categorySettingsFormFields
        );
      }
    });
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
   * By default we redirect to home on close event (you can override this on child component)
   */
  protected closeWizard(): void {
    this.router.navigate(['/home']);
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

  /**
   * Does the current step is the widget configuration step or not
   *
   * @param step The step
   */
  protected isWidgetConfigurationStep(step: FormStep): boolean {
    return step.imageLink != null || step.description != null || step.information != null;
  }

  /**
   * Hook used to save the wizard
   * Implemented by child component
   *
   * @param formData The value of the form
   */
  protected saveWizard(formData: FormData): void {}
}
