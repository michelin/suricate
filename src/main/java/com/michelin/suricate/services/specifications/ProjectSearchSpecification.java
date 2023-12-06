package com.michelin.suricate.services.specifications;


import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.Project_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


/**
 * Project search specification.
 */
public class ProjectSearchSpecification extends AbstractSearchSpecification<Project> {
    public ProjectSearchSpecification(final String search) {
        super(search);
    }

    /**
     * Used to add search predicates.
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Project> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        // Add a predicate for each word in the research string.
        // Allow the user to retrieve projects with multiple consecutive whitespaces
        // in the title even by typing a single whitespace
        if (StringUtils.isNotBlank(search)) {
            for (String keyWord : search.split("\\s+")) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Project_.name)),
                    String.format(LIKE_OPERATOR_FORMATTER, keyWord.toLowerCase())));
            }
        }
    }
}
