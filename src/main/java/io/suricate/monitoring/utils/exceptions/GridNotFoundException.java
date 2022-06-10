package io.suricate.monitoring.utils.exceptions;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

public class GridNotFoundException extends ApiException {
    /**
     * Grid not found object
     */
    private static final String MSG_GRID_NOT_FOUND = "Grid ''{0}'' not found for project {1}";

    /**
     * Constructor
     * @param gridId The grid ID
     * @param projectToken The project token
     */
    public GridNotFoundException(Object gridId, String projectToken) {
        super(MessageFormat.format(MSG_GRID_NOT_FOUND, gridId.toString(), projectToken), ApiErrorEnum.OBJECT_NOT_FOUND);
    }
}
