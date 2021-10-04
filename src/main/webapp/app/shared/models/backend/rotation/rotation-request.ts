import { RotationProjectRequest } from '../rotation-project/rotation-project-request';

/**
 * The rotation request
 */
export interface RotationRequest {
  /**
   * The rotation name
   */
  name: string;

  /**
   * List of rotation project requests
   */
  rotationProjectRequests?: RotationProjectRequest[];
}
