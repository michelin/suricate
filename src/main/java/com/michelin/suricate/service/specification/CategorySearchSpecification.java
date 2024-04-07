package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Category_;


/**
 * Category search specification.
 */
public class CategorySearchSpecification extends AbstractSearchSpecification<Category> {

    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public CategorySearchSpecification(final String search) {
        super(search, Category_.name);
    }
}
