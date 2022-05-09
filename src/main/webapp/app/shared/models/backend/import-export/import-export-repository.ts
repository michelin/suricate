import {RepositoryTypeEnum} from "../../../enums/repository-type.enum";
import {ImportExportCategoryParameter} from "./import-export-category-parameter";
import {ImportExportCategory} from "./import-export-category";

export class ImportExportRepository {
    /**
     * The repository name
     */
    name: string;

    /**
     * The repository url
     */
    url: string;

    /**
     * The repository branch to clone
     */
    branch: string;

    /**
     * The login to use for the connection to the remote repository
     */
    login: string;

    /**
     * The password to use for the connection to the remote repository
     */
    password: string;

    /**
     * The path of the repository in case of a local folder
     */
    localPath: string;

    /**
     * The type of repository
     */
    type: RepositoryTypeEnum;

    /**
     * If the repository is enabled or not
     */
    enabled: boolean;

    /**
     * The categories
     */
    categories: ImportExportCategory[];

    /**
     * Constructor
     */
    constructor() {}
}