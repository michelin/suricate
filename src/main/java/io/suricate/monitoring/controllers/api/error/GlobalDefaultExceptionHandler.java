/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.controllers.api.error;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.utils.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * Manage Rest exceptions
 */
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    /**
     * The Logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    /**
     * Manage the API Exception
     *
     * @param ex The exception
     * @return The exception as Response Entity
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ex.getError().getStatus())
            .body(ex.getError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleRequestException(MethodArgumentNotValidException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(extractMessage(ex.getBindingResult()), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Throw when a user try access a resource that he can't
     *
     * @param ex the exception
     * @return The related response entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ApiErrorEnum.FORBIDDEN.getStatus())
            .body(new ApiErrorDto(ApiErrorEnum.FORBIDDEN));
    }

    /**
     * Throw when a user try to access a resource with a not supported Http Verb
     *
     * @param ex The exception
     * @return The response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDto> handleRequestException(HttpRequestMethodNotSupportedException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(ex.getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the unknown exception
     *
     * @param ex the exception
     * @return The related response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleException(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity
            .status(ApiErrorEnum.INTERNAL_SERVER_ERROR.getStatus())
            .body(new ApiErrorDto(ApiErrorEnum.INTERNAL_SERVER_ERROR));
    }

    /**
     * Exception handler for {@link ConstraintViolationException} raised when
     * Spring fails to validate a bean in the service layer.
     *
     * @param ex the exception being raised
     * @return the response entity
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiErrorDto> handleRequestException(ConstraintViolationException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(ex.getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Exception handler for {@link DataIntegrityViolationException} raised when
     * Hibernate validators fail to validate a bean in the JPA layer.
     *
     * @param ex the exception being raised
     * @return the response entity
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorDto> handleRequestException(DataIntegrityViolationException ex) {
        LOGGER.debug(ex.getMessage());
        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(Objects.requireNonNull(ex.getRootCause()).getMessage(), ApiErrorEnum.BAD_REQUEST));
    }


    /**
     * Method used to extract message from MethodArgumentNotValidException exception
     *
     * @param bindingResult Binding result
     * @return an error string
     */
    private static String extractMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(error.getField()).append(' ').append(error.getDefaultMessage());
        }
        return builder.toString();
    }


}
