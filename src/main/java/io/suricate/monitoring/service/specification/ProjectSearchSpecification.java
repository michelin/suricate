package io.suricate.monitoring.service.specification;


import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.Project_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Class used to filter JPA queries
 */
public class ProjectSearchSpecification extends AbstractSearchSpecification<Project> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public ProjectSearchSpecification(final String search) {
        super(search, Project_.name);
    }

    /**
     * Used to add search predicates
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Project> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        // Add a predicate for each word in the research string. Allow the user to retrieve projects with multiple consecutive whitespaces in the title even by typing a single whitespace
        if (StringUtils.isNotBlank(search)) {
            for (String keyWord : search.split("\\s+")) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Project_.name)), String.format(LIKE_OPERATOR_FORMATTER, keyWord.toLowerCase())));
            }
        }
    }
}
