package com.michelin.suricate.utils.exceptions;

import com.michelin.suricate.model.enums.ApiErrorEnum;
import java.text.MessageFormat;

/**
 * Project token invalid exception.
 */
public class ProjectTokenInvalidException extends ApiException {
    private static final String MSG_PROJECT_TOKEN_INVALID = "Cannot decrypt token : {0}";

    /**
     * Constructor.
     *
     * @param projectToken The token
     */
    public ProjectTokenInvalidException(final String projectToken) {
        super(MessageFormat.format(MSG_PROJECT_TOKEN_INVALID, projectToken), ApiErrorEnum.PROJECT_TOKEN_INVALID);
    }
}
