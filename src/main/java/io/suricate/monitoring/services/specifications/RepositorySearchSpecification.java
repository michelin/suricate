package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.model.entities.Repository_;


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
