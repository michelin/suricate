package io.suricate.monitoring.utils.exceptions;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

public class OperationNotPermittedException extends ApiException {
    /**
     * Message for Object not found
     */
    private static final String MSG_OPERATION_NOT_PERMITTED = "This operation is not permitted: {0}";

    public OperationNotPermittedException(String cause) {
        super(MessageFormat.format(MSG_OPERATION_NOT_PERMITTED, cause), ApiErrorEnum.OPERATION_NOT_PERMITTED);
    }
}
