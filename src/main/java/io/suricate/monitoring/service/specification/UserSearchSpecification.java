package io.suricate.monitoring.service.specification;


import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.entity.user.User_;


/**
 * Class used to filter JPA queries
 */
public class UserSearchSpecification extends AbstractSearchSpecification<User> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public UserSearchSpecification(final String search) {
        super(search, User_.username);
    }
}
