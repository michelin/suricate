package io.suricate.monitoring.service.specification;


import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.Project_;


/**
 * Class used to filter JPA queries
 */
public class ProjectSearchSpecification extends AbstractSearchSpecification<Project> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public ProjectSearchSpecification(final String search) {
        super(search, Project_.name);
    }
}
