package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Category_;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.entity.widget.Widget_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;


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
        super(search, Widget_.name);
    }

    /**
     * Search widgets by category name
     *
     * @param categoryName  The name of the category to search by
     * @return A widget specification
     */
    public static Specification<Widget> getWidgetByCategoryNameSpecification(final String categoryName) {
        String categoryNameFilter = categoryName != null ? "%" + categoryName.toLowerCase() + "%" : null;

        return (root, query, criteriaBuilder) -> {
            Join<Widget, Category> join = root.join(Widget_.category);

            return criteriaBuilder.like(
                    criteriaBuilder.lower(join.get(Category_.name)), categoryNameFilter);
        };
    }
}
