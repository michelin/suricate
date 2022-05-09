import {CategoryParameter} from "../category-parameters/category-parameter";
import {ImportExportCategoryParameter} from "./import-export-category-parameter";
import {ImportExportAsset} from "./import-export-asset";

export class ImportExportCategory {
    name: string;
    technicalName: string;
    image: ImportExportAsset;
    categoryParameters: ImportExportCategoryParameter[];

    /**
     * Constructor
     */
    constructor() {}
}