package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Category_;


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
