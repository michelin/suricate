import {GridProperties} from "./grid-properties";
import {ProjectGrid} from "../project-grid/project-grid";

/**
 * Import project form data class
 */
export class ImportProjectRequest {
    name: string;
    gridProperties: GridProperties
    displayProgressBar: boolean;
    grids: ProjectGrid[];
    importFile: string;

    /**
     * Constructor
     */
    constructor() {}
}