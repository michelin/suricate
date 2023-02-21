package com.michelin.suricate.services.specifications;

import com.michelin.suricate.model.entities.CategoryParameter;
import com.michelin.suricate.model.entities.CategoryParameter_;

public class CategoryParametersSearchSpecification extends AbstractSearchSpecification<CategoryParameter> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public CategoryParametersSearchSpecification(final String search) {
        super(search, CategoryParameter_.description);
    }
}
