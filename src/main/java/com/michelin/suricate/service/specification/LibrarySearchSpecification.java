package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Library_;

/**
 * Library search specification.
 */
public class LibrarySearchSpecification extends AbstractSearchSpecification<Library> {
    /**
     * Constructor.
     *
     * @param search The search query
     */
    public LibrarySearchSpecification(final String search) {
        super(search, Library_.technicalName);
    }
}
