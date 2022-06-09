import { Component, OnInit } from '@angular/core';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';

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
  constructor() {}

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
      title: 'settings.my'
    };
  }
}
