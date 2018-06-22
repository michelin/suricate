import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from '../configuration.service';
import {ActivatedRoute} from '@angular/router';
import {Configuration} from '../../../shared/model/dto/Configuration';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

/**
 * Manage the edition of a configuration
 */
@Component({
  selector: 'app-configuration-edit',
  templateUrl: './configuration-edit.component.html',
  styleUrls: ['./configuration-edit.component.css']
})
export class ConfigurationEditComponent implements OnInit {

  /**
   * The edit form
   */
  configurationForm: FormGroup;

  /**
   * The current configuration
   */
  configuration: Configuration;

  /**
   * Constructor
   *
   * @param {ActivatedRoute} _activatedRoute The activated route service
   * @param {FormBuilder} _formBuilder The form builder
   * @param {ConfigurationService} _configurationService The configuration service
   */
  constructor(private _activatedRoute: ActivatedRoute,
              private _formBuilder: FormBuilder,
              private _configurationService: ConfigurationService) {
  }

  ngOnInit() {
    this._activatedRoute.params.subscribe(params => {
      this._configurationService.getOneByKey(params['configurationKey']).subscribe(configuration => {
        this.configuration = configuration;
        this.initConfigForm();
      });
    });
  }

  /**
   * Init the configuration form
   */
  initConfigForm() {
    this.configurationForm = this._formBuilder.group({
      key: [this.configuration.key, [Validators.required]],
      value: [this.configuration.value ? this.configuration.value : '', [Validators.required]],
      category: [this.configuration.category.name, Validators.required]
    });
  }

  /**
   * Save the configuration
   */
  saveConfiguration() {

  }

  /**
   * Check if the field is invalid
   *
   * @param {string} field The field to check
   * @returns {boolean} False if the field valid, true otherwise
   */
  isFieldInvalid(field: string) {
    return this.configurationForm.invalid && (this.configurationForm.get(field).dirty || this.configurationForm.get(field).touched);
  }

}
