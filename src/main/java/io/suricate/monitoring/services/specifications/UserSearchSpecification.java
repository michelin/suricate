package io.suricate.monitoring.services.specifications;


import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.User_;


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
