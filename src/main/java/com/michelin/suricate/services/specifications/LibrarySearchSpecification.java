package com.michelin.suricate.services.specifications;

import com.michelin.suricate.model.entities.Library;
import com.michelin.suricate.model.entities.Library_;

public class LibrarySearchSpecification extends AbstractSearchSpecification<Library> {
    /**
     * Constructor
     * @param search The search query
     */
    public LibrarySearchSpecification(final String search) { super(search, Library_.technicalName); }
}
