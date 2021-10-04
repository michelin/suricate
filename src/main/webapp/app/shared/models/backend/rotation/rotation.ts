/**
 * Rotation class
 */
import { RotationProject } from '../rotation-project/rotation-project';

export class Rotation {
  /**
   * The rotation name
   */
  name: string;

  /**
   * The rotation token
   */
  token: string;

  /**
   * The list of projects rotating
   */
  rotationProjects: RotationProject[] = [];

  /**
   * Constructor
   */
  constructor() {}
}
