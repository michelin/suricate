import { KtdGridCompactType } from '@katoid/angular-grid-layout';

export interface GridOptions {
  cols: number;
  rowHeight: number;
  gap: number;
  draggable: boolean;
  resizable: boolean;
  compactType: KtdGridCompactType;
}