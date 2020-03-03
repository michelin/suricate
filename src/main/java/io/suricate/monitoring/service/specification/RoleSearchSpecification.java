package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.model.entity.user.Role_;


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
