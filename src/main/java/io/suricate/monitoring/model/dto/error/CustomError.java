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

package io.suricate.monitoring.model.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.suricate.monitoring.model.enums.ApiErrorEnum;

public class CustomError {

    /**
     * Constructor using fields
     *
     * @param message the error message
     * @param key     the error key
     * @param code    the error code
     */
    public CustomError(String message, String key, int code) {
        this.message = message;
        this.code = code;
        this.key = key;
    }

    /**
     * Constructor with ApiErrorEnum object
     *
     * @param apiErrorEnum api error object
     */
    public CustomError(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.code = apiErrorEnum.getOrdinal();
        this.key = apiErrorEnum.getKey();
    }

    private String message;

    private int code;

    private String key;


    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public int getCode() {
        return code;
    }

    @JsonProperty
    public String getKey() {
        return key;
    }

}
