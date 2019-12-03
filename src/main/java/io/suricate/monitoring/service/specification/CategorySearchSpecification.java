package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Category_;


/**
 * Class used to filter JPA queries
 */
public class CategorySearchSpecification extends AbstractSearchSpecification<Category> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public CategorySearchSpecification(final String search) {
        super(search, Category_.name);
    }
}
