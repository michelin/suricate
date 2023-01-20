/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.controllers.handlers;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.utils.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {
    private static final String LOG_MESSAGE = "An exception has occurred in the API controllers part";

    /**
     * Manage the API exception.
     * @param exception The exception
     * @return The exception as Response Entity
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(exception.getError().getStatus())
            .body(exception.getError());
    }

    /**
     * Manage the API exception.
     * @param exception The exception
     * @return The exception as Response Entity
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(BadCredentialsException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
                .status(ApiErrorEnum.BAD_CREDENTIALS_ERROR.getStatus())
                .body(new ApiErrorDto(ApiErrorEnum.BAD_CREDENTIALS_ERROR));
    }

    /**
     * Manage the MethodArgumentNotValidException exception.
     * @param exception The exception
     * @return The exception as Response Entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleRequestException(MethodArgumentNotValidException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(extractMessage(exception.getBindingResult()), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the AccessDeniedException exception.
     * Throw when a user try access a resource that he can't.
     * @param exception the exception
     * @return The related response entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDeniedException(AccessDeniedException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(ApiErrorEnum.FORBIDDEN.getStatus())
            .body(new ApiErrorDto(ApiErrorEnum.FORBIDDEN));
    }

    /**
     * Manage the HttpRequestMethodNotSupportedException exception.
     * Throw when a user try to access a resource with a not supported Http Verb.
     * @param exception The exception
     * @return The response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDto> handleRequestException(HttpRequestMethodNotSupportedException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(exception.getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the ConstraintViolationException exception.
     * Throw when Spring fails to validate a bean in the service layer.
     * @param exception the exception being raised
     * @return the response entity
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiErrorDto> handleRequestException(ConstraintViolationException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(exception.getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the DataIntegrityViolationException exception.
     * Throw when Hibernate validators fail to validate a bean in the JPA layer.
     * @param exception the exception being raised
     * @return the response entity
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorDto> handleRequestException(DataIntegrityViolationException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
            .status(ApiErrorEnum.BAD_REQUEST.getStatus())
            .body(new ApiErrorDto(Objects.requireNonNull(exception.getRootCause()).getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the HttpMediaTypeNotAcceptableException exception.
     * @param exception The exception
     * @return The exception as Response Entity
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiErrorDto> handleRequestException(HttpMediaTypeNotAcceptableException exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
                .status(ApiErrorEnum.BAD_REQUEST.getStatus())
                .body(new ApiErrorDto(Objects.requireNonNull(exception.getRootCause()).getMessage(), ApiErrorEnum.BAD_REQUEST));
    }

    /**
     * Manage the default Exception exception.
     * @param exception the exception
     * @return The related response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleException(Exception exception) {
        log.debug(GlobalDefaultExceptionHandler.LOG_MESSAGE, exception);

        return ResponseEntity
                .status(ApiErrorEnum.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ApiErrorDto(ApiErrorEnum.INTERNAL_SERVER_ERROR));
    }

    /**
     * Method used to extract message from MethodArgumentNotValidException exception
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
