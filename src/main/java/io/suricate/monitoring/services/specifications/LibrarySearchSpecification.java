package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Library_;
import io.suricate.monitoring.model.entities.Role;

import javax.persistence.metamodel.SingularAttribute;

public class LibrarySearchSpecification extends AbstractSearchSpecification<Library> {
    /**
     * Constructor
     * @param search The search query
     */
    public LibrarySearchSpecification(final String search) { super(search, Library_.technicalName); }
}
