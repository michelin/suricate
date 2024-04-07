package com.michelin.suricate.util.exception;

import com.michelin.suricate.model.enumeration.ApiErrorEnum;
import java.text.MessageFormat;

/**
 * Object not found exception.
 */
public class ObjectNotFoundException extends ApiException {
    private static final String MSG_OBJECT_NOT_FOUND = "{0} ''{1}'' not found";

    /**
     * Constructor.
     *
     * @param entity The entity class
     * @param id     The object id
     */
    public ObjectNotFoundException(Class<?> entity, Object id) {
        super(MessageFormat.format(MSG_OBJECT_NOT_FOUND, entity.getSimpleName(), id.toString()),
            ApiErrorEnum.OBJECT_NOT_FOUND);
    }
}
