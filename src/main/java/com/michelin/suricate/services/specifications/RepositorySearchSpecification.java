package com.michelin.suricate.services.specifications;

import com.michelin.suricate.model.entities.Repository;
import com.michelin.suricate.model.entities.Repository_;


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
