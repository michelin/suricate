package io.suricate.monitoring.utils.exception;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

/**
 * Exception throw when the requested object is not found
 */
public class InvalidFileException extends ApiException {

    /**
     * Message for Object not found
     */
    private static final String MSG_OBJECT_INVALID_FILE = "The file {0} cannot be read for entity {1} '{2}'";

    /**
     * @param filename The filename
     * @param entity   The entity class
     * @param id       The object id
     */
    public InvalidFileException(String filename, Class<?> entity, Object id) {
        super(MessageFormat.format(MSG_OBJECT_INVALID_FILE, filename, entity.getSimpleName(), id.toString()), ApiErrorEnum.FILE_ERROR);
    }
}
