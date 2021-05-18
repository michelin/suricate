package io.suricate.monitoring.utils.exceptions;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

/**
 * Manage the no content on fetching list
 */
public class NoContentException extends ApiException {
    /**
     * Message for no content
     */
    private static final String MSG_LIST_NO_CONTENT = "No resource for the class ''{0}''";

    /**
     * Constructor
     *
     * @param entity The entity to find
     */
    public NoContentException(final Class<?> entity) {
        super(MessageFormat.format(MSG_LIST_NO_CONTENT, entity.getSimpleName()), ApiErrorEnum.NO_CONTENT);
    }
}
