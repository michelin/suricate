package com.michelin.suricate.services.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

/**
 * Abstract search specification.
 *
 * @param <T> The type of the specification
 */
@Getter
public abstract class AbstractSearchSpecification<T> implements Specification<T> {
    protected static final String LIKE_OPERATOR_FORMATTER = "%%%s%%";
    protected final String search;
    private final List<String> attributes;

    /**
     * Constructor.
     *
     * @param search           The search query
     * @param filterAttributes The attribute used to filter on search attribute
     */
    protected AbstractSearchSpecification(final String search, final SingularAttribute<T, String>... filterAttributes) {
        this.search = search;
        this.attributes = Arrays.stream(filterAttributes).map(Attribute::getName).collect(Collectors.toList());
    }

    /**
     * Used to add search predicates.
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    protected void addSearchPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            String likeSearchString = String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase());
            Optional
                .ofNullable(attributes)
                .orElseGet(ArrayList::new)
                .forEach((String attribute) -> predicates.add(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(attribute)), likeSearchString)));
        }
    }

    /**
     * Used to add predicates to the search query.
     *
     * @param root            The root entity
     * @param criteriaQuery   Used to build queries
     * @param criteriaBuilder Used to build new predicate
     */
    @Override
    public Predicate toPredicate(@NotNull Root<T> root, @NotNull CriteriaQuery<?> criteriaQuery,
                                 @NotNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        addSearchPredicate(root, criteriaBuilder, predicates);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
