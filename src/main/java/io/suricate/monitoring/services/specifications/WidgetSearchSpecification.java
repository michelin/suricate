package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Category_;
import io.suricate.monitoring.model.entities.Widget;
import io.suricate.monitoring.model.entities.Widget_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.*;
import java.util.List;


/**
 * Class used to filter JPA queries
 */
public class WidgetSearchSpecification extends AbstractSearchSpecification<Widget> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public WidgetSearchSpecification(final String search) {
        super(search);
    }

    /**
     * Used to add search predicates
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Widget> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            Predicate searchByName = criteriaBuilder.like(criteriaBuilder.lower(root.get(Widget_.name)), String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByCategoryName = widgetByCategoryName(root, criteriaBuilder);

            predicates.add(criteriaBuilder.or(searchByName, searchByCategoryName));
        }
    }

    /**
     * Add a predicate which allow widgets to be searched by the category name
     *
     * @param root The root entity
     * @param criteriaBuilder The criteria builder
     * @return A predicate on the category name
     */
    private Predicate widgetByCategoryName(Root<Widget> root, CriteriaBuilder criteriaBuilder) {
        Join<Widget, Category> join = root.join(Widget_.category);

        return criteriaBuilder.like(
                criteriaBuilder.lower(join.get(Category_.name)), String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
    }
}
