import {GridProperties} from "../project/grid-properties";
import {Asset} from "../asset/asset";
import {ProjectGrid} from "../project-grid/project-grid";
import {ImportExportAsset} from "./import-export-asset";
import {ImportExportProjectGrid} from "./import-export-project-grid";

export class ImportExportProject {
    name: string;
    displayProgressBar: boolean;
    image: ImportExportAsset;
    gridProperties: GridProperties = new GridProperties();
    grids: ImportExportProjectGrid[];

    /**
     * Constructor
     */
    constructor() {}
}