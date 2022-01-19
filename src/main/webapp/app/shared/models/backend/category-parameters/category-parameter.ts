import { DataTypeEnum } from '../../../enums/data-type.enum';
import { Category } from '../category/category';

/**
 * The category parameter entity
 */
export class CategoryParameter {
  key: string;
  value: string;
  description: string;
  export: boolean;
  dataType: DataTypeEnum;
  category: Category;
}
