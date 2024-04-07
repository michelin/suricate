package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.CategoryParameter_;

/**
 * Category parameters search specification.
 */
public class CategoryParametersSearchSpecification extends AbstractSearchSpecification<CategoryParameter> {

    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public CategoryParametersSearchSpecification(final String search) {
        super(search, CategoryParameter_.description);
    }
}
