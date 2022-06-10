package io.suricate.monitoring.utils.exceptions;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

public class RepositorySyncException extends ApiException {
    /**
     * Message for Object not found
     */
    private static final String MSG_REPOSITORY_EXCEPTION = "Fail to synchronize repository {0} {1}/{2}";

    /**
     * Constructor
     * @param branch The repository branch
     * @param url The repository url
     * @param name The repository name
     */
    public RepositorySyncException(String name, String url, String branch) {
        super(MessageFormat.format(MSG_REPOSITORY_EXCEPTION, name, url, branch), ApiErrorEnum.BAD_REQUEST);
    }
}
