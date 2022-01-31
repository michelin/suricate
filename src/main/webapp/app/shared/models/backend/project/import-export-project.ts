import {GridProperties} from "./grid-properties";
import {Asset} from "../asset/asset";
import {ImportExportProjectGrid} from "../project-grid/import-export-project-grid";

/**
 * Import project form data class
 */
export class ImportExportProject {
    name: string;
    gridProperties: GridProperties
    displayProgressBar: boolean;
    grids: ImportExportProjectGrid[];
    image: Asset;
    importFile: string;

    /**
     * Constructor
     */
    constructor() {}
}