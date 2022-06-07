import { Component, OnInit } from '@angular/core';
import {IconEnum} from "../../../shared/enums/icon.enum";
import {ButtonTypeEnum} from "../../../shared/enums/button-type.enum";
import {HeaderConfiguration} from "../../../shared/models/frontend/header/header-configuration";
import {FormGroup} from "@angular/forms";
import {AuthenticationService} from "../../../shared/services/frontend/authentication/authentication.service";
import {UserSetting} from "../../../shared/models/backend/setting/user-setting";
import {SettingsService} from "../../services/settings.service";
import {
  SettingsFormFieldsService
} from "../../../shared/services/frontend/form-fields/settings-form-fields/settings-form-fields.service";
import {FormField} from "../../../shared/models/frontend/form/form-field";
import {FormService} from "../../../shared/services/frontend/form/form.service";
import {ButtonConfiguration} from "../../../shared/models/frontend/button/button-configuration";
import {from} from "rxjs";
import {flatMap, toArray} from "rxjs/operators";
import {Setting} from "../../../shared/models/backend/setting/setting";
import {UserSettingRequest} from "../../../shared/models/backend/setting/user-setting-request";
import {AllowedSettingValue} from "../../../shared/models/backend/setting/allowed-setting-value";
import {HttpUserService} from "../../../shared/services/backend/http-user/http-user.service";

@Component({
  selector: 'suricate-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  /**
   * Configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * Constructor
   */
  constructor() { }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initHeaderConfiguration();
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'settings.my',
    };
  }
}
