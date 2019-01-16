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

package io.suricate.monitoring.model.dto.api.widget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.WidgetVariableType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a widget param response used for communication with the clients via webservices
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "WidgetParam", description = "Describe the params for an instance of widget")
public class WidgetParamResponseDto extends AbstractDto {
    /**
     * The param name
     */
    @ApiModelProperty(value = "The param name", required = true)
    private String name;

    /**
     * The param description
     */
    @ApiModelProperty(value = "Describe how to set this param", required = true)
    private String description;

    /**
     * The default value of the param
     */
    @ApiModelProperty(value = "HTML Default value to insert on the field")
    private String defaultValue;

    /**
     * The param type {@link WidgetVariableType}
     */
    @ApiModelProperty(value = "The type of this param define the HTML element to display", required = true)
    private WidgetVariableType type;

    /**
     * The regex used for accept a file while uploading it if the type is a FILE
     */
    @ApiModelProperty(value = "A regex to respect for the field")
    private String acceptFileRegex;

    /**
     * An exemple of the usage of this param
     */
    @ApiModelProperty(value = "An example of the usage of this field")
    private String usageExample;

    /**
     * If the param is required True by default
     */
    @ApiModelProperty(value = "If the field is required or not", required = true)
    private boolean required = true;

    /**
     * The list of param values if the type is COMBO or a MULTIPLE
     */
    @ApiModelProperty(value = "The list of possible values if the type is COMBO or MULTIPLE", dataType = "java.util.List")
    private List<WidgetParamValueResponseDto> values = new ArrayList<>();
}
