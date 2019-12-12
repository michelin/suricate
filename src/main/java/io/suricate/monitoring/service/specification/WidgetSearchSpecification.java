package io.suricate.monitoring.service.specification;

import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.entity.widget.Widget_;


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
}
