import { GridRequest } from './grid-request';

export class ProjectGridRequest {
  [key: string]: any;
  displayProgressBar: boolean;
  grids: GridRequest[];
}
