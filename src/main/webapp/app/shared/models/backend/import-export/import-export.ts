import {ImportExportRepository} from "./import-export-repository";
import {ImportExportLibrary} from "./import-export-library";
import {ImportExportProject} from "./import-export-project";

export class ImportExport {
    repositories: ImportExportRepository[];
    libraries: ImportExportLibrary[];
    projects: ImportExportProject[];
    
    /**
     * Constructor
     */
    constructor() {}
}
