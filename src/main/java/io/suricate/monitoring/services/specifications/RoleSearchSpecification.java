package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.Role_;

/**
 * Class used to filter JPA queries
 */
public class RoleSearchSpecification extends AbstractSearchSpecification<Role> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public RoleSearchSpecification(final String search) {
        super(search, Role_.name);
    }
}
