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

package io.suricate.monitoring.model.dto.api;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Send the application properties needed by the frontend
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "ApplicationProperties", description = "Hold application properties information")
public class ApplicationPropertiesDto extends AbstractDto {

    /**
     * The propertie key
     */
    @ApiModelProperty(value = "The propertie key")
    private String key;

    /**
     * The current value
     */
    @ApiModelProperty(value = "The current value")
    private String value;

    /**
     * A little description of this param
     */
    @ApiModelProperty(value = "A little description of this param")
    private String description;

    /**
     * Args constructor
     *
     * @param key         The key
     * @param value       The value
     * @param description The description
     */
    public ApplicationPropertiesDto(final String key, final String value, final String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }
}
