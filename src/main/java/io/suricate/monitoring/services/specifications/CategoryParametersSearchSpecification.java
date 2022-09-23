package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.model.entities.CategoryParameter_;

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
