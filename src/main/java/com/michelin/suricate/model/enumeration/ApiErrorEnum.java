/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.model.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum used to define the error type.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ApiErrorEnum {
    NO_CONTENT("No Content", "no.content", HttpStatus.NO_CONTENT),
    BAD_REQUEST("Bad request", "bad.request", HttpStatus.BAD_REQUEST),
    PROJECT_TOKEN_INVALID("Cannot decrypt project token", "project.token.invalid", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("Authentication error, token expired or invalid", "authentication.error",
        HttpStatus.UNAUTHORIZED),
    NOT_AUTHORIZED("User not authorized", "not.authorized", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS_ERROR("Bad credentials", "authentication.bad.credentials", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("You don't have permission to access to this resource", "user.forbidden", HttpStatus.FORBIDDEN),
    FILE_ERROR("File cannot be read", "file.cannot.read", HttpStatus.INTERNAL_SERVER_ERROR),
    OBJECT_NOT_FOUND("Object not found", "object.not.found", HttpStatus.NOT_FOUND),
    OBJECT_ALREADY_EXIST("Object already exist", "object.already.exist", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXIST("Username already exist", "username.already.exist", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXIST("Email already exist", "email.already.exist", HttpStatus.CONFLICT),
    OPERATION_NOT_PERMITTED("Operation not permitted", "operation.not.permitted", HttpStatus.CONFLICT),
    PRECONDITION_FAILED("Precondition failed for this request", "precondition.failed", HttpStatus.PRECONDITION_FAILED),
    INTERNAL_SERVER_ERROR("Internal Server Error", "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int ordinal;
    private final String key;
    private final HttpStatus status;

    /**
     * Constructor.
     *
     * @param message The message
     * @param key     The key
     * @param status  The HttpStatus
     */
    ApiErrorEnum(String message, String key, HttpStatus status) {
        this.status = status;
        this.message = message;
        this.ordinal = ordinal();
        this.key = key;
    }

    public ApiErrorDto toResponse(String message) {
        return new ApiErrorDto(message, this);
    }
}
