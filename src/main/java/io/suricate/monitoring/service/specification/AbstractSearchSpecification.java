package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Used to build JPA Queries
 */
public abstract class AbstractSearchSpecification<T> implements Specification<T> {

    /**
     * Like formatter
     */
    private static final String LIKE_OPERATOR_FORMATTER = "%%%s%%";

    /**
     * The list of attributes to use to filter the Object
     */
    private final List<String> attributes;

    /**
     * The string to search
     */
    private final String search;

    /**
     * Constructor
     *
     * @param search           The search query
     * @param filterAttributes The attribute used to filter on search attribute
     */
    public AbstractSearchSpecification(final String search, final SingularAttribute<User, String>... filterAttributes) {
        this.search = search;
        this.attributes = Arrays.stream(filterAttributes).map(Attribute::getName).collect(Collectors.toList());
    }

    /**
     * Used to add search predicates
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    private void addSearchPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            String likeSearchString = String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase());
            Optional
                .ofNullable(attributes)
                .orElseGet(ArrayList::new)
                .forEach((String attribute) -> predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(attribute)), likeSearchString)));
        }
    }

    /**
     * Used to add predicates to the search query
     *
     * @param root            The root entity
     * @param criteriaQuery   Used to build queries
     * @param criteriaBuilder Used to build new predicate
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        addSearchPredicate(root, criteriaBuilder, predicates);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
