package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.WidgetConfiguration;
import io.suricate.monitoring.model.entity.WidgetConfiguration_;


/**
 * Class used to filter JPA queries
 */
public class WidgetConfigurationSearchSpecification extends AbstractSearchSpecification<WidgetConfiguration> {

    /**
     * Constructor
     *
     * @param search The string to search
     */
    public WidgetConfigurationSearchSpecification(final String search) {
        super(search, WidgetConfiguration_.key);
    }
}
