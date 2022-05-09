import {DataTypeEnum} from "../../../enums/data-type.enum";
import {Category} from "../category/category";

export class ImportExportCategoryParameter {
    key: string;
    value: string;
    description: string;
    export: boolean;
    dataType: DataTypeEnum;

    /**
     * Constructor
     */
    constructor() {}
}