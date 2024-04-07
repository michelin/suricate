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

package com.michelin.suricate.model.dto.api.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.ApiErrorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Api error DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Api error response")
public class ApiErrorDto extends AbstractDto {
    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Error key")
    private String key;

    @Schema(description = "HttpStatus number", example = "1")
    private int status;

    @Schema(description = "Date of the error")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    /**
     * Constructor.
     *
     * @param apiErrorEnum The api error enum
     */
    public ApiErrorDto(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.key = apiErrorEnum.getKey();
        this.timestamp = new Date();
        this.status = apiErrorEnum.getStatus().value();
    }

    /**
     * Constructor.
     *
     * @param message  The error message
     * @param apiError The API error enum
     */
    public ApiErrorDto(String message, ApiErrorEnum apiError) {
        this(apiError);
        this.message = StringUtils.isBlank(message) ? apiError.getMessage() : message;
    }
}
