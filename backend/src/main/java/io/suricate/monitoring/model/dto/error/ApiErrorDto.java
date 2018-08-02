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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Used for send errors through webservices
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "ApiError", description = "Api error response")
public class ApiErrorDto extends AbstractDto {
    /**
     * The error message to send
     */
    @ApiModelProperty(value = "Error message")
    private String message;
    /**
     * The key code
     */
    @ApiModelProperty(value = "Error key")
    private String key;
    /**
     * The HttpStatus number
     */
    @ApiModelProperty(value = "HttpStatus number")
    private int status;
    /**
     * The datetime of the error
     */
    @ApiModelProperty(value = "Date of the error")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;


    public ApiErrorDto(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.key = apiErrorEnum.getKey();
        this.timestamp = new Date();
        this.status = apiErrorEnum.getStatus().value();
    }

    public ApiErrorDto(String message, ApiErrorEnum apiError) {
        this(apiError);
        this.message = StringUtils.isBlank(message) ? apiError.getMessage() : message;
    }

}
