/**
 * Rotation class
 */
import {RotationProject} from "../rotation-project/rotation-project";

export class Rotation {
    /**
     * The rotation id
     */
    id: number;

    /**
     * The rotation name
     */
    name: string;

    /**
     * The list of projects rotating
     */
    rotationProjects: RotationProject[] = [];

    /**
     * Constructor
     */
    constructor() {}
}