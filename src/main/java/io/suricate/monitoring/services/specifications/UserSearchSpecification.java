package io.suricate.monitoring.services.specifications;


import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.User_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
        super(search);
    }

    /**
     * Used to add search predicates
     *
     * Add research by username, firstname or lastname
     *
     * Override the default addSearchPredicate which performs "and" operator instead of "or"
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<User> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            Predicate searchByUsername = criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.username)), String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByFirstname = criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.firstname)), String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByLastname = criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.lastname)), String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));

            predicates.add(criteriaBuilder.or(searchByUsername, searchByFirstname, searchByLastname));
        }
    }
}
