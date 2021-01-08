package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.WidgetConfiguration;
import io.suricate.monitoring.model.entities.WidgetConfiguration_;


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
