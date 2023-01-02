import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormField } from '../../../../shared/models/frontend/form/form-field';
import { ButtonConfiguration } from '../../../../shared/models/frontend/button/button-configuration';
import { UserSetting } from '../../../../shared/models/backend/setting/user-setting';
import { AuthenticationService } from '../../../../shared/services/frontend/authentication/authentication.service';
import { IconEnum } from '../../../../shared/enums/icon.enum';
import { from } from 'rxjs';
import { flatMap, toArray } from 'rxjs/operators';
import { Setting } from '../../../../shared/models/backend/setting/setting';
import { UserSettingRequest } from '../../../../shared/models/backend/setting/user-setting-request';
import { AllowedSettingValue } from '../../../../shared/models/backend/setting/allowed-setting-value';
import { SettingsService } from '../../../services/settings.service';
import { SettingsFormFieldsService } from '../../../../shared/services/frontend/form-fields/settings-form-fields/settings-form-fields.service';
import { FormService } from '../../../../shared/services/frontend/form/form.service';
import { HttpUserService } from '../../../../shared/services/backend/http-user/http-user.service';

@Component({
  selector: 'suricate-ux-settings',
  templateUrl: './ux-settings.component.html',
  styleUrls: ['./ux-settings.component.scss']
})
export class UxSettingsComponent implements OnInit {
  /**
   * The form group for UX settings
   */
  public formGroup: FormGroup;

  /**
   * The form fields for UX settings
   */
  public formFields: FormField[];

  /**
   * The buttons
   */
  public buttons: ButtonConfiguration<unknown>[] = [];

  /**
   * The user settings
   */
  public userSettings: UserSetting[];

  /**
   * Constructor
   * @param settingsService The settings service
   * @param settingsFormFieldsService The settings form fields service
   * @param formService The form service
   * @param httpUserService The http user service
   */
  constructor(
    private readonly settingsService: SettingsService,
    private readonly settingsFormFieldsService: SettingsFormFieldsService,
    private readonly formService: FormService,
    private readonly httpUserService: HttpUserService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initButtons();

    this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe((userSettings: UserSetting[]) => {
      this.userSettings = userSettings;
      this.settingsFormFieldsService.generateSettingsFormFields(userSettings).subscribe((formFields: FormField[]) => {
        this.formFields = formFields;
        this.formGroup = this.formService.generateFormGroupForFields(formFields);
      });
    });
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.buttons.push({
      label: 'save',
      icon: IconEnum.SAVE,
      color: 'primary',
      callback: () => this.save()
    });
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.formService.validate(this.formGroup);

    if (this.formGroup.valid) {
      this.saveSettings(this.formGroup.value);
    }
  }

  /**
   * Save the selected settings
   *
   * @param formData The selected settings from the form
   */
  private saveSettings(formData: FormData): void {
    from(this.userSettings.map(userSetting => userSetting.setting))
      .pipe(
        flatMap((setting: Setting) => {
          const userSettingRequest = new UserSettingRequest();
          if (setting.constrained && setting.allowedSettingValues) {
            const selectedAllowedSetting = setting.allowedSettingValues.find((allowedSettingValue: AllowedSettingValue) => {
              return allowedSettingValue.settingValue === formData[setting.type];
            });

            userSettingRequest.allowedSettingValueId = selectedAllowedSetting.id;
          } else {
            userSettingRequest.unconstrainedValue = formData[setting.type];
          }

          return this.httpUserService.updateUserSetting(AuthenticationService.getConnectedUser().username, setting.id, userSettingRequest);
        }),
        toArray()
      )
      .subscribe(() => {
        this.settingsService.initUserSettings(AuthenticationService.getConnectedUser()).subscribe((userSettings: UserSetting[]) => {
          this.userSettings = userSettings;
        });
      });
  }
}
