package com.michelin.suricate.utils.exceptions;

import com.michelin.suricate.model.enums.ApiErrorEnum;
import java.text.MessageFormat;

/**
 * Invalid file exception.
 */
public class InvalidFileException extends ApiException {
    private static final String MSG_OBJECT_INVALID_FILE = "The file {0} cannot be read for entity {1} ''{2}''";

    /**
     * Constructor.
     *
     * @param filename The filename
     * @param entity   The entity class
     * @param id       The object id
     */
    public InvalidFileException(String filename, Class<?> entity, Object id) {
        super(MessageFormat.format(MSG_OBJECT_INVALID_FILE, filename, entity.getSimpleName(), id.toString()),
            ApiErrorEnum.FILE_ERROR);
    }
}
