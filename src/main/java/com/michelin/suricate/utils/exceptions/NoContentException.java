package com.michelin.suricate.utils.exceptions;

import com.michelin.suricate.model.enums.ApiErrorEnum;
import java.text.MessageFormat;

/**
 * No content exception.
 */
public class NoContentException extends ApiException {
    private static final String MSG_LIST_NO_CONTENT = "No resource for the class ''{0}''";

    /**
     * Constructor.
     *
     * @param entity The entity to find
     */
    public NoContentException(final Class<?> entity) {
        super(MessageFormat.format(MSG_LIST_NO_CONTENT, entity.getSimpleName()), ApiErrorEnum.NO_CONTENT);
    }
}
