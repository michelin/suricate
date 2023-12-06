/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.utils.exceptions;


import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.enums.ApiErrorEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Api exception.
 */
public class ApiException extends RuntimeException {
    private final ApiErrorEnum error;

    /**
     * Constructor.
     *
     * @param error the API error object to store into the exception
     */
    public ApiException(ApiErrorEnum error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Constructor.
     *
     * @param message custom message
     * @param error   the API error object to store into the exception
     */
    public ApiException(String message, ApiErrorEnum error) {
        super(StringUtils.isBlank(message) ? error.getMessage() : message);
        this.error = error;
    }


    /**
     * Method used to retrieve the error.
     *
     * @return the APi error
     */
    public ApiErrorDto getError() {
        return error.toResponse(getMessage());
    }
}
