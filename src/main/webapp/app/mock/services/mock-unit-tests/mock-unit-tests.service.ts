import { Injectable } from '@angular/core';
import { FormField } from '../../../shared/models/frontend/form/form-field';
import { DataTypeEnum } from '../../../shared/enums/data-type.enum';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { WizardConfiguration } from '../../../shared/models/frontend/wizard/wizard-configuration';
import { FormStep } from '../../../shared/models/frontend/form/form-step';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { Project } from '../../../shared/models/backend/project/project';
import { ProjectGrid } from '../../../shared/models/backend/project/project-grid';

@Injectable({
  providedIn: 'root'
})
export class MockUnitTestsService {
  /**
   * Constructor
   *
   * @param formBuilder The form builder
   */
  constructor(private readonly formBuilder: FormBuilder) {}

  /**
   * Build a mocked project object for the unit tests
   */
  public buildMockedProject(): Project {
    const gridProperties: ProjectGrid = {
      maxColumn: 5,
      widgetHeight: 300,
      cssStyle: ''
    };

    return {
      gridProperties: gridProperties,
      librariesToken: ['Token1', 'Token2'],
      name: 'ProjectName',
      screenshotToken: 'ScreenToken',
      token: 'Token'
    };
  }

  /**
   * Build a mocked FormField for the unit tests
   *
   * @param type The type of the field to create
   */
  public buildMockedFormField(type: DataTypeEnum): FormField {
    return {
      key: 'Key',
      type: type
    };
  }

  /**
   * Build a mocked FormGroup for the unit tests
   *
   * @param type The type of the field to control
   */
  public buildMockedFormGroup(type: DataTypeEnum): FormGroup {
    const customField = this.buildMockedFormField(type);

    const formGroup: FormGroup = this.formBuilder.group({});
    formGroup.addControl(customField.key, new FormControl(customField.value));

    return formGroup;
  }

  /**
   * Build a mocked FormArray for the unit tests
   *
   * @param type The type of the field to control
   */
  public buildMockedFormArray(type: DataTypeEnum): FormArray {
    return this.buildMockedFormGroup(type).controls[this.buildMockedFormField(type).key] as FormArray;
  }

  /**
   * Build a mocked WizardConfiguration for the unit tests
   */
  public buildWizardConfiguration(): WizardConfiguration {
    const formSteps: FormStep[] = [];

    for (let i = 0; i < 3; i++) {
      formSteps.push({
        key: 'Key' + i,
        title: 'Title' + i,
        icon: IconEnum.ADD
      });
    }

    return {
      steps: formSteps
    };
  }
}
