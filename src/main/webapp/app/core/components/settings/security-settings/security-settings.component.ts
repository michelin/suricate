import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {FormField} from "../../../../shared/models/frontend/form/form-field";
import {DataTypeEnum} from "../../../../shared/enums/data-type.enum";
import {FormService} from "../../../../shared/services/frontend/form/form.service";
import {IconEnum} from "../../../../shared/enums/icon.enum";
import {ButtonConfiguration} from "../../../../shared/models/frontend/button/button-configuration";
import {HttpUserService} from "../../../../shared/services/backend/http-user/http-user.service";
import {Token} from "../../../../shared/models/backend/token/token";
import {Clipboard} from '@angular/cdk/clipboard';
import {ToastTypeEnum} from "../../../../shared/enums/toast-type.enum";
import {ToastService} from "../../../../shared/services/frontend/toast/toast.service";
import {HttpErrorResponse} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";
import {TokenRequest} from "../../../../shared/models/backend/token/token-request";
import {MaterialIconRecords} from "../../../../shared/records/material-icon.record";

@Component({
  selector: 'suricate-security-settings',
  templateUrl: './security-settings.component.html',
  styleUrls: ['./security-settings.component.scss']
})
export class SecuritySettingsComponent implements OnInit {
  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The form group for UX settings
   */
  public formGroup: FormGroup;

  /**
   * The form fields for UX settings
   */
  public formFields: FormField[] = [];

  /**
   * The buttons
   */
  public generateTokenButton: ButtonConfiguration<unknown>;

  /**
   * The buttons
   */
  public copyTokenButton: ButtonConfiguration<unknown>;

  /**
   * The created token
   */
  public createdToken: Token;

  /**
   * The user tokens
   */
  public tokens: Token[];

  /**
   * Constructor
   * @param formService The form service
   * @param toastService The toast service
   * @param translateService The translate service
   * @param httpUserService The http user service
   * @param clipboard The clipboard service
   */
  constructor(private readonly formService: FormService,
              private readonly toastService: ToastService,
              private readonly translateService: TranslateService,
              private readonly httpUserService: HttpUserService,
              private clipboard: Clipboard) { }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initButtons();
    this.initFormFields();

    this.httpUserService.getUserTokens().subscribe((tokens: Token[]) => {
      this.tokens = tokens;
    });
  }

  /**
   * Init the buttons
   */
  private initButtons(): void {
    this.generateTokenButton = {
        label: 'settings.security.generate.tokens.button.label',
        icon: IconEnum.SAVE,
        color: 'primary',
        callback: () => this.save()
      };

    this.copyTokenButton = {
      label: 'copy',
      icon: IconEnum.COPY,
      color: 'primary',
      callback: () => this.copy()
    }
  }

  /**
   * Init the security settings form fields
   */
  initFormFields(): void {
    this.formFields.push({
      key: 'name',
      label: 'settings.security.token.name.field',
      type: DataTypeEnum.TEXT,
      validators: [Validators.required]
    });

    this.formGroup = this.formService.generateFormGroupForFields(this.formFields);
  }

  /**
   * Execute save action on click
   */
  private save(): void {
    this.formService.validate(this.formGroup);

    if (this.formGroup.valid) {
      const tokenRequest: TokenRequest = this.formGroup.value;
      this.httpUserService.createToken(tokenRequest).subscribe((token: Token) => {
        this.createdToken = token;
      }, (error: HttpErrorResponse) => {
        if (error.status === 400) {
          this.toastService.sendMessage(this.translateService.instant('settings.security.token.created.duplicated.name', { tokenName: tokenRequest.name }), ToastTypeEnum.DANGER);
        }
      });
    }
  }

  /**
   * Copy the generated token to clipboard
   */
  public copy(): void {
    const copied = this.clipboard.copy(this.createdToken.value)

    if (copied) {
      this.toastService.sendMessage('copy.success', ToastTypeEnum.SUCCESS);
    }
  }
}
