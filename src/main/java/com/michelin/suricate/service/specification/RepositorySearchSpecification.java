package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Repository_;


/**
 * Repository search specification.
 */
public class RepositorySearchSpecification extends AbstractSearchSpecification<Repository> {

    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public RepositorySearchSpecification(final String search) {
        super(search, Repository_.name);
    }
}
