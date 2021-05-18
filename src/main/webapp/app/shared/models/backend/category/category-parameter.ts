import { DataTypeEnum } from '../../../enums/data-type.enum';
import { Category } from '../widget/category';

/**
 * The category parameter entity
 */
export class CategoryParameter {
  key: string;
  value: string;
  export: boolean;
  dataType: DataTypeEnum;
  category: Category;
}
