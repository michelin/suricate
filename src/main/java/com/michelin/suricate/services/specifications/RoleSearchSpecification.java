package com.michelin.suricate.services.specifications;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.Role_;

/**
 * Role search specification.
 */
public class RoleSearchSpecification extends AbstractSearchSpecification<Role> {
    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public RoleSearchSpecification(final String search) {
        super(search, Role_.name);
    }
}
