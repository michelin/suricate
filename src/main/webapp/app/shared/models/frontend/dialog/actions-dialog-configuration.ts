import { DialogConfiguration } from './dialog-configuration';
import { ButtonConfiguration } from '../button/button-configuration';

/**
 * Configuration used by the actions dialog
 */
export class ActionsDialogConfiguration extends DialogConfiguration {
  /**
   * Used to add actions buttons
   */
  actions: ButtonConfiguration<unknown>[];
}
