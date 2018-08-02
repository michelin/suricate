package io.suricate.monitoring.utils.exception;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

/**
 * Throw when the project token cannot be decrypt
 */
public class ProjectTokenInvalidException extends ApiException {

    /**
     * Message for Project Token invalid
     */
    private static final String MSG_PROJECT_TOKEN_INVALID = "Cannot decrypt token : {0}";

    /**
     * Constructor
     *
     * @param projectToken The token
     */
    public ProjectTokenInvalidException(final String projectToken) {
        super(MessageFormat.format(MSG_PROJECT_TOKEN_INVALID, projectToken), ApiErrorEnum.PROJECT_TOKEN_INVALID);
    }
}
