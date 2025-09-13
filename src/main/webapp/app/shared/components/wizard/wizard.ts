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

import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatStepper } from '@angular/material/stepper';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonColorEnum } from '../../enums/button-color.enum';
import { ButtonConfiguration } from '../../models/frontend/button/button-configuration';
import { FormField } from '../../models/frontend/form/form-field';
import { FormStep } from '../../models/frontend/form/form-step';
import { ValueChangedEvent } from '../../models/frontend/form/value-changed-event';
import { HeaderConfiguration } from '../../models/frontend/header/header-configuration';
import { MaterialIconRecords } from '../../models/frontend/icon/material-icon';
import { WizardConfiguration } from '../../models/frontend/wizard/wizard-configuration';
import { FormService } from '../../services/frontend/form/form.service';
import { WidgetConfigurationFormFieldsService } from '../../services/frontend/form-fields/widget-configuration-form-fields/widget-configuration-form-fields.service';
import { ProjectWidgetFormStepsService } from '../../services/frontend/form-steps/project-widget-form-steps/project-widget-form-steps.service';

/**
 * Generic component used to display wizards
 */
@Component({
	template: '',
	styleUrls: ['./wizard.scss'],
	standalone: true
})
export class Wizard implements OnInit {
	private readonly formService = inject(FormService);
	private readonly widgetConfigurationFormFieldsService = inject(WidgetConfigurationFormFieldsService);
	protected readonly activatedRoute = inject(ActivatedRoute);
	protected readonly router = inject(Router);

	/**
	 * Reference on the stepper
	 */
	@ViewChild('wizardStepper', { static: true })
	public wizardStepper: MatStepper;

	/**
	 * The token of the dashboard
	 */
	public dashboardToken: string;

	/**
	 * The id of the dashboard grid
	 */
	public gridId: number;

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
	private stepperFormGroup: UntypedFormGroup;

	/**
	 * The list of material icons
	 */
	public materialIconRecords = MaterialIconRecords;

	/**
	 * The current step
	 */
	public currentStep: FormStep;

	/**
	 * Called when the component is init
	 */
	public ngOnInit(): void {
		this.dashboardToken = this.activatedRoute.snapshot.params['dashboardToken'];
		this.gridId = Number(this.activatedRoute.snapshot.params['gridId']);

		this.initWizardButtons();
		this.stepperFormGroup = this.formService.generateFormGroupForSteps(this.wizardConfiguration.steps);
		this.currentStep = this.wizardConfiguration.steps[0];
	}

	/**
	 * Init the buttons of the wizard
	 */
	private initWizardButtons(): void {
		this.wizardButtons = [
			{
				label: 'close',
				color: ButtonColorEnum.WARN,
				callback: () => this.closeWizard()
			},
			{
				label: 'back',
				hidden: () => !this.shouldDisplayBackButton(),
				callback: () => this.backAction()
			},
			{
				label: 'next',
				hidden: () => !this.shouldDisplayNextButton(),
				callback: () => this.nextAction()
			},
			{
				label: 'done',
				hidden: () => !this.shouldDisplayDoneButton(),
				callback: () => this.validateFormBeforeSave()
			}
		];
	}

	/**
	 * If we have async fields on the new step we load them
	 *
	 * @param stepperSelectionEvent The step change event
	 */
	public onStepChanged(stepperSelectionEvent: StepperSelectionEvent): void {
		// When backing to previous step, mark current step as not interacted to avoid input error issues
		if (stepperSelectionEvent.previouslySelectedIndex > stepperSelectionEvent.selectedIndex) {
			stepperSelectionEvent.previouslySelectedStep.interacted = false;
		}

		this.currentStep = this.wizardConfiguration.steps[stepperSelectionEvent.selectedIndex];

		if (this.currentStep?.asyncFields) {
			this.currentStep
				.asyncFields(stepperSelectionEvent.selectedStep.stepControl as unknown as UntypedFormGroup, this.currentStep)
				.subscribe((formFields: FormField[]) => {
					this.currentStep.fields = formFields;
					this.stepperFormGroup.setControl(
						this.currentStep.key,
						this.formService.generateFormGroupForFields(formFields)
					);
				});
		}
	}

	/**
	 * Called when a value of the form has changed
	 *
	 * @param valueChangeEvent The value change event
	 */
	public onValueChanged(valueChangeEvent: ValueChangedEvent): void {
		if (valueChangeEvent.type === 'mosaicOptionSelected' && !this.shouldDisplayDoneButton()) {
			setTimeout(() => this.wizardStepper.next(), 500);
		}
	}

	/**
	 * Add the settings of the widget's category to the current widget settings form
	 *
	 * @param event The values retrieved from the child component event emitter
	 */
	public displayCategorySettings(event: MatSlideToggleChange): void {
		this.widgetConfigurationFormFieldsService.addOrRemoveCategoryParametersFormFields(
			this.currentStep.category.categoryParameters,
			event.checked,
			this.stepperFormGroup.controls[this.currentStep.key] as UntypedFormGroup,
			this.currentStep.fields
		);
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
		return this.wizardStepper?.steps && this.wizardStepper.selectedIndex < this.wizardStepper.steps.length - 1;
	}

	/**
	 * Used to know if the back button should be displayed
	 */
	private shouldDisplayDoneButton(): boolean {
		return this.wizardStepper?.steps && this.wizardStepper.selectedIndex === this.wizardStepper.steps.length - 1;
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
	public getFormGroupOfStep(step: FormStep): UntypedFormGroup {
		return this.stepperFormGroup.controls[step.key] as UntypedFormGroup;
	}

	/**
	 * Does the current step is the widget configuration step or not
	 */
	public isWidgetConfigurationStep(): boolean {
		return this.currentStep.key === ProjectWidgetFormStepsService.configureWidgetStepKey;
	}

	/**
	 * Hook used to save the wizard
	 *
	 * @param ignoredFormGroup The form group
	 */
	protected saveWizard(ignoredFormGroup: UntypedFormGroup): void {
		// Implemented by child component
	}

	/**
	 * Check if the stepper form is valid before saving the data
	 */
	protected validateFormBeforeSave(): void {
		this.formService.validate(this.stepperFormGroup);

		if (this.stepperFormGroup.valid) {
			this.saveWizard(this.stepperFormGroup);
		}
	}
}
