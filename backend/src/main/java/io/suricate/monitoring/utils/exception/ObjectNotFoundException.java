package io.suricate.monitoring.utils.exception;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

/**
 * Exception throw when the requested object is not found
 */
public class ObjectNotFoundException extends ApiException {

    /**
     * Message for Object not found
     */
    private static final String MSG_OBJECT_NOT_FOUND = "{0} ''{1}'' not found";

    /**
     * @param entity The entity class
     * @param id     The object id
     */
    public ObjectNotFoundException(Class<?> entity, Object id) {
        super(MessageFormat.format(MSG_OBJECT_NOT_FOUND, entity.getSimpleName(), id.toString()), ApiErrorEnum.OBJECT_NOT_FOUND);
    }
}
