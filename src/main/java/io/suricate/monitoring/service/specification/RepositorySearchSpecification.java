package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.widget.Repository;
import io.suricate.monitoring.model.entity.widget.Repository_;


/**
 * Class used to filter JPA queries
 */
public class RepositorySearchSpecification extends AbstractSearchSpecification<Repository> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public RepositorySearchSpecification(final String search) {
        super(search, Repository_.name);
    }
}
