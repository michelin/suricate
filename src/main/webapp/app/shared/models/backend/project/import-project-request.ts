import {GridProperties} from "./grid-properties";
import {ProjectGrid} from "../project-grid/project-grid";
import {Asset} from "../asset/asset";

/**
 * Import project form data class
 */
export class ImportProjectRequest {
    name: string;
    gridProperties: GridProperties
    displayProgressBar: boolean;
    grids: ProjectGrid[];
    image: Asset;
    importFile: string;

    /**
     * Constructor
     */
    constructor() {}
}