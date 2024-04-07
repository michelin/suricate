package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.Role_;

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
