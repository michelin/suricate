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

package io.suricate.monitoring.model.dto.api.widgetconfiguration;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.category.CategoryResponseDto;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuration used for communication with the clients via webservices
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ConfigurationResponse", description = "Describe a configuration")
public class WidgetConfigurationResponseDto extends AbstractDto {

    /**
     * The configuration key
     */
    @ApiModelProperty(value = "The configuration key")
    private String key;

    /**
     * The configuration value
     */
    @ApiModelProperty(value = "The configuration value")
    private String value;

    /**
     * The data type of the configuration
     */
    @ApiModelProperty(value = "Configuration data type")
    private DataTypeEnum dataType;

    /**
     * Make a link between category and configurations
     */
    @ApiModelProperty(value = "Related category for this config")
    private CategoryResponseDto category;
}
